package com.capstone.realestate.service.impl;

import com.capstone.realestate.dto.PropertyCriteria;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private BrokerRepository brokerRepository;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Test
    void addProperty_WhenBrokerMissing_ShouldThrow() {
        when(brokerRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.addProperty(new Property(), 3));
    }

    @Test
    void addProperty_WhenValid_ShouldSetBrokerAndSave() {
        Broker broker = new Broker();
        broker.setBroId(5);

        Property p = new Property();
        p.setOfferCost(100);
        p.setAreaSqft(200);

        when(brokerRepository.findById(5)).thenReturn(Optional.of(broker));
        when(propertyRepository.save(any(Property.class))).thenAnswer(inv -> inv.getArgument(0));

        Property out = propertyService.addProperty(p, 5);

        assertEquals(5, out.getBroker().getBroId());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void editProperty_WhenNotOwner_ShouldThrow() {
        Broker owner = new Broker();
        owner.setBroId(11);
        Property existing = new Property();
        existing.setBroker(owner);

        when(propertyRepository.findById(8)).thenReturn(Optional.of(existing));

        assertThrows(AccessDeniedException.class, () -> propertyService.editProperty(8, new Property(), 9));
    }

    @Test
    void removeProperty_WhenMissing_ShouldThrow() {
        when(propertyRepository.findById(9)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.removeProperty(9));
    }

    @Test
    void listPropertyByCriteria_ShouldDelegate() {
        PropertyCriteria criteria = new PropertyCriteria("2BHK", "SALE", "Chennai", 10, 200);
        when(propertyRepository.findByCriteria("2BHK", "SALE", "Chennai", 10, 200)).thenReturn(List.of(new Property()));

        List<Property> out = propertyService.listPropertyByCriteria(criteria);

        assertEquals(1, out.size());
    }
}
