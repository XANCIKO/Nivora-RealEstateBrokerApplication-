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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerAdditionalTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PaymentController paymentController;

    private void mockStripeResponse() {
        RestTemplate restTemplate = org.mockito.Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(paymentController, "restTemplate", restTemplate);
        Map<String, Object> stripeResponse = new HashMap<>();
        stripeResponse.put("id", "pi_123");
        stripeResponse.put("client_secret", "cs_123");
        when(restTemplate.postForObject(eq("https://api.stripe.com/v1/payment_intents"), any(), eq(Map.class)))
                .thenReturn(stripeResponse);
    }

    @Test
    void createIntent_WhenStripeMissing_ShouldThrow() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "");
        assertThrows(IllegalArgumentException.class, () -> paymentController.createIntent(1));
    }

    @Test
    void createIntent_WhenSaleValid_ShouldReturnSuccess() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test");
        mockStripeResponse();
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(100000);
        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));

        var response = paymentController.createIntent(1);

        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void createIntent_WhenSaleValid_ShouldSetCreatedMessage() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test");
        mockStripeResponse();
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(100000);
        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));

        var response = paymentController.createIntent(1);

        assertEquals("Payment intent created.", response.getBody().getMessage());
    }

    @Test
    void createIntent_WhenRentValid_ShouldReturnPayload() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test");
        mockStripeResponse();
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("RENT");
        p.setOfferCost(10000);
        when(propertyRepository.findById(2)).thenReturn(Optional.of(p));

        var response = paymentController.createIntent(2);

        assertNotNull(response.getBody().getData());
    }

    @Test
    void createIntent_WhenRentValid_ShouldSet100PercentAdvance() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test");
        mockStripeResponse();
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("RENT");
        p.setOfferCost(10000);
        when(propertyRepository.findById(2)).thenReturn(Optional.of(p));

        var response = paymentController.createIntent(2);

        assertEquals(100.0, response.getBody().getData().getAdvancePercent());
    }

    @Test
    void createIntent_WhenSaleShouldCap_ShouldSetFlagTrue() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test");
        mockStripeResponse();
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(2_000_000);
        when(propertyRepository.findById(3)).thenReturn(Optional.of(p));

        var response = paymentController.createIntent(3);

        assertTrue(response.getBody().getData().isCappedByGatewayLimit());
    }

    @Test
    void createIntent_WhenSaleSmall_ShouldSetFlagFalse() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test");
        mockStripeResponse();
        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(10000);
        when(propertyRepository.findById(4)).thenReturn(Optional.of(p));

        var response = paymentController.createIntent(4);

        assertFalse(response.getBody().getData().isCappedByGatewayLimit());
    }

    @Test
    void createIntent_WhenStripeReturnsNull_ShouldThrow() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test");
        RestTemplate restTemplate = org.mockito.Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(paymentController, "restTemplate", restTemplate);

        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(10000);
        when(propertyRepository.findById(5)).thenReturn(Optional.of(p));
        when(restTemplate.postForObject(eq("https://api.stripe.com/v1/payment_intents"), any(), eq(Map.class)))
                .thenReturn(null);

        assertThrows(IllegalStateException.class, () -> paymentController.createIntent(5));
    }

    @Test
    void createIntent_WhenStripeMissingClientSecret_ShouldThrow() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test");
        RestTemplate restTemplate = org.mockito.Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(paymentController, "restTemplate", restTemplate);

        Property p = new Property();
        p.setStatus(true);
        p.setOfferType("SALE");
        p.setOfferCost(10000);
        when(propertyRepository.findById(6)).thenReturn(Optional.of(p));

        Map<String, Object> noSecret = new HashMap<>();
        noSecret.put("id", "pi_only");
        when(restTemplate.postForObject(eq("https://api.stripe.com/v1/payment_intents"), any(), eq(Map.class)))
                .thenReturn(noSecret);

        assertThrows(IllegalStateException.class, () -> paymentController.createIntent(6));
    }
}
