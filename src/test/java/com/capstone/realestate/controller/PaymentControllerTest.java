package com.capstone.realestate.controller;

import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void createIntent_ShouldThrowWhenStripeNotConfigured() {
        assertThrows(IllegalArgumentException.class, () -> paymentController.createIntent(1));
    }

    @Test
    void createIntent_ShouldThrowWhenPropertyMissing() {
        ReflectionTestUtils.setField(paymentController, "stripeSecretKey", "sk_test_mock");
        when(propertyRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentController.createIntent(999));
    }
}
