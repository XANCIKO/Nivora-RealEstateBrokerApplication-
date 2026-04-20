package com.capstone.realestate.controller;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.entity.User;
import com.capstone.realestate.repository.BrokerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.service.IPropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {

    @Mock
    private IPropertyService propertyService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BrokerRepository brokerRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private PropertyController propertyController;

    @Test
    void searchProperties_ShouldDelegateToService() {
        when(propertyService.listPropertyByCriteria(any())).thenReturn(List.of(new Property()));

        var response = propertyController.searchProperties("2BHK", "SALE", "Chennai", 1000, 5000);

        assertNotNull(response);
        verify(propertyService, times(1)).listPropertyByCriteria(any());
    }

    @Test
    void listAll_ShouldReturnResponse() {
        when(propertyService.listAllProperties()).thenReturn(List.of(new Property()));

        var response = propertyController.listAll();

        assertNotNull(response);
        verify(propertyService, times(1)).listAllProperties();
    }

    @Test
    void addProperty_ShouldResolveBrokerAndDelegate() {
        User u = new User();
        u.setUserId(10);
        u.setEmail("broker@example.com");

        Broker b = new Broker();
        b.setBroId(20);

        Property p = new Property();
        p.setCity("Chennai");

        when(userDetails.getUsername()).thenReturn("broker@example.com");
        when(userRepository.findByEmailIgnoreCase("broker@example.com")).thenReturn(Optional.of(u));
        when(brokerRepository.findByUser_UserId(10)).thenReturn(Optional.of(b));
        when(propertyService.addProperty(p, 20)).thenReturn(p);

        var response = propertyController.addProperty(p, userDetails);

        assertNotNull(response);
        verify(propertyService, times(1)).addProperty(p, 20);
    }

    @Test
    void editProperty_ShouldPassPathIdAndBrokerId() {
        User u = new User();
        u.setUserId(10);
        Broker b = new Broker();
        b.setBroId(20);

        Property payload = new Property();

        when(userDetails.getUsername()).thenReturn("broker@example.com");
        when(userRepository.findByEmailIgnoreCase("broker@example.com")).thenReturn(Optional.of(u));
        when(brokerRepository.findByUser_UserId(10)).thenReturn(Optional.of(b));
        when(propertyService.editProperty(7, payload, 20)).thenReturn(payload);

        var response = propertyController.editProperty(7, payload, userDetails);

        assertNotNull(response);
        verify(propertyService, times(1)).editProperty(7, payload, 20);
    }

    @Test
    void deleteProperty_ShouldDelegateToService() {
        when(propertyService.removeProperty(8)).thenReturn(new Property());

        var response = propertyController.deleteProperty(8);

        assertNotNull(response);
        verify(propertyService, times(1)).removeProperty(8);
    }

    @Test
    void myListings_ShouldResolveBrokerIdAndDelegate() {
        User u = new User();
        u.setUserId(55);
        Broker b = new Broker();
        b.setBroId(66);

        when(userDetails.getUsername()).thenReturn("broker@example.com");
        when(userRepository.findByEmailIgnoreCase("broker@example.com")).thenReturn(Optional.of(u));
        when(brokerRepository.findByUser_UserId(55)).thenReturn(Optional.of(b));
        when(propertyService.listPropertiesByBroker(66)).thenReturn(List.of(new Property()));

        var response = propertyController.myListings(userDetails);

        assertNotNull(response);
        verify(propertyService, times(1)).listPropertiesByBroker(66);
    }
}
