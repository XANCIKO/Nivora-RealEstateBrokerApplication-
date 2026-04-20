package com.capstone.realestate.controller;

import com.capstone.realestate.entity.Property;
import com.capstone.realestate.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerEdgeTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void createIntent_WhenPropertyUnavailable_ShouldThrow() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test_x");

        Property p = new Property();
        p.setStatus(false);
        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));

        assertThrows(IllegalArgumentException.class, () -> paymentController.createIntent(1));
    }

    @Test
    void createIntent_WhenAmountInvalid_ShouldThrow() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test_x");

        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(0);
        when(propertyRepository.findById(2)).thenReturn(Optional.of(p));

        assertThrows(IllegalArgumentException.class, () -> paymentController.createIntent(2));
    }

    @Test
    void createIntent_WhenRentExceedsLimit_ShouldThrow() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test_x");

        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("RENT");
        p.setOfferCost(2_000_000.0);
        when(propertyRepository.findById(3)).thenReturn(Optional.of(p));

        assertThrows(IllegalArgumentException.class, () -> paymentController.createIntent(3));
    }

    @Test
    void createIntent_WhenSaleAboveGatewayCap_ShouldStillReturnResponse() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test_x");

        RestTemplate restTemplate = org.mockito.Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(paymentController, "restTemplate", restTemplate);

        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(2_000_000.0); // 8% => 160,000 INR, above Stripe INR cap
        when(propertyRepository.findById(4)).thenReturn(Optional.of(p));

        Map<String, Object> stripeResponse = new HashMap<>();
        stripeResponse.put("id", "pi_test_123");
        stripeResponse.put("client_secret", "cs_test_123");

        when(restTemplate.postForObject(eq("https://api.stripe.com/v1/payment_intents"), any(), eq(Map.class)))
                .thenReturn(stripeResponse);

        var response = paymentController.createIntent(4);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
    }
}
