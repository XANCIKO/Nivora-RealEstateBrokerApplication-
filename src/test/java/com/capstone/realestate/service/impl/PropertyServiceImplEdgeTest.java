package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.BrokerRepository;
import com.capstone.realestate.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplEdgeTest {

    @Mock private PropertyRepository propertyRepository;
    @Mock private BrokerRepository brokerRepository;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Test
    void addProperty_WhenNegativeOfferCost_ShouldThrow() {
        Broker b = new Broker();
        when(brokerRepository.findById(1)).thenReturn(Optional.of(b));

        Property p = new Property();
        p.setOfferCost(-1);
        p.setAreaSqft(100);

        assertThrows(IllegalArgumentException.class, () -> propertyService.addProperty(p, 1));
    }

    @Test
    void addProperty_WhenNegativeArea_ShouldThrow() {
        Broker b = new Broker();
        when(brokerRepository.findById(1)).thenReturn(Optional.of(b));

        Property p = new Property();
        p.setOfferCost(10);
        p.setAreaSqft(-100);

        assertThrows(IllegalArgumentException.class, () -> propertyService.addProperty(p, 1));
    }

    @Test
    void editProperty_WhenPropertyMissing_ShouldThrow() {
        when(propertyRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.editProperty(99, new Property(), 1));
    }

    @Test
    void editProperty_WhenNegativePayload_ShouldThrow() {
        Broker owner = new Broker();
        owner.setBroId(3);
        Property existing = new Property();
        existing.setBroker(owner);
        when(propertyRepository.findById(6)).thenReturn(Optional.of(existing));

        Property payload = new Property();
        payload.setOfferCost(-10);
        payload.setAreaSqft(100);

        assertThrows(IllegalArgumentException.class, () -> propertyService.editProperty(6, payload, 3));
    }

    @Test
    void editProperty_WhenOwnerAndValid_ShouldUpdateExisting() {
        Broker owner = new Broker();
        owner.setBroId(8);

        Property existing = new Property();
        existing.setBroker(owner);

        Property payload = new Property();
        payload.setConfiguration("2BHK");
        payload.setOfferType("SALE");
        payload.setOfferCost(1200);
        payload.setAreaSqft(900);
        payload.setAddress("A");
        payload.setStreet("S");
        payload.setCity("C");

        when(propertyRepository.findById(2)).thenReturn(Optional.of(existing));
        when(propertyRepository.save(any(Property.class))).thenAnswer(inv -> inv.getArgument(0));

        Property out = propertyService.editProperty(2, payload, 8);

        assertEquals("2BHK", out.getConfiguration());
        assertEquals("SALE", out.getOfferType());
        verify(propertyRepository, times(1)).save(existing);
    }

    @Test
    void editProperty_WhenWrongOwner_ShouldThrowAccessDenied() {
        Broker owner = new Broker();
        owner.setBroId(10);
        Property existing = new Property();
        existing.setBroker(owner);
        when(propertyRepository.findById(3)).thenReturn(Optional.of(existing));

        assertThrows(AccessDeniedException.class, () -> propertyService.editProperty(3, new Property(), 11));
    }
}
