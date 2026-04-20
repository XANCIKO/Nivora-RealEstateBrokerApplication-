package com.capstone.realestate.controller;

import com.capstone.realestate.dto.ApiResponse;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.PropertyRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private static final double SALE_ADVANCE_PERCENT = 8.0;
    // Keep a tiny buffer below Stripe's hard INR max to avoid edge rounding issues.
    private static final long STRIPE_MAX_AMOUNT_PAISE = 99_999_900L;

    private final PropertyRepository propertyRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stripe.secret-key:}")
    private String stripeSecretKey;

    @PostMapping("/intent/{propId}")
    public ResponseEntity<ApiResponse<PaymentIntentResponse>> createIntent(@PathVariable int propId) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new IllegalArgumentException("Stripe is not configured. Add stripe.secret-key in backend properties.");
        }

        Property property = propertyRepository.findById(propId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propId));

        if (!property.isStatus()) {
            throw new IllegalArgumentException("Property is no longer available for purchase/rent.");
        }

        double fullAmountInInr = property.getOfferCost();
        boolean saleOffer = "SALE".equalsIgnoreCase(property.getOfferType());
        double targetPayableAmountInInr = saleOffer
                ? (fullAmountInInr * SALE_ADVANCE_PERCENT / 100.0)
                : fullAmountInInr;

        long requestedAmountInPaise = Math.round(targetPayableAmountInInr * 100);
        if (requestedAmountInPaise <= 0) {
            throw new IllegalArgumentException("Invalid property amount for payment.");
        }

        boolean cappedByGatewayLimit = false;
        long amountInPaise = requestedAmountInPaise;
        if (saleOffer && requestedAmountInPaise > STRIPE_MAX_AMOUNT_PAISE) {
            amountInPaise = STRIPE_MAX_AMOUNT_PAISE;
            cappedByGatewayLimit = true;
        }

        if (!saleOffer && requestedAmountInPaise > STRIPE_MAX_AMOUNT_PAISE) {
            throw new IllegalArgumentException("Booking amount exceeds Stripe transaction limit. Please contact support to complete this booking offline.");
        }

        double payableAmountInInr = amountInPaise / 100.0;
        double effectiveAdvancePercent = saleOffer
                ? (payableAmountInInr / fullAmountInInr) * 100.0
                : 100.0;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(stripeSecretKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("amount", String.valueOf(amountInPaise));
        body.add("currency", "inr");
        body.add("automatic_payment_methods[enabled]", "true");
        body.add("metadata[propId]", String.valueOf(propId));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        @SuppressWarnings("unchecked")
        Map<String, Object> stripeRes = restTemplate.postForObject(
                "https://api.stripe.com/v1/payment_intents",
                request,
                Map.class
        );

        if (stripeRes == null || stripeRes.get("client_secret") == null) {
            throw new IllegalStateException("Could not create Stripe payment intent.");
        }

        PaymentIntentResponse response = new PaymentIntentResponse(
                String.valueOf(stripeRes.get("id")),
                String.valueOf(stripeRes.get("client_secret")),
                amountInPaise,
                "inr",
                fullAmountInInr,
                payableAmountInInr,
                Math.max(0.0, fullAmountInInr - payableAmountInInr),
                effectiveAdvancePercent,
                cappedByGatewayLimit,
                saleOffer ? SALE_ADVANCE_PERCENT : 100.0
        );

        return ResponseEntity.ok(ApiResponse.success("Payment intent created.", response));
    }

    @Data
    @AllArgsConstructor
    static class PaymentIntentResponse {
        private String paymentIntentId;
        private String clientSecret;
        private long amount;
        private String currency;
        private double fullAmountInInr;
        private double payableAmountInInr;
        private double remainingAmountInInr;
        private double advancePercent;
        private boolean cappedByGatewayLimit;
        private double targetAdvancePercent;
    }
}
