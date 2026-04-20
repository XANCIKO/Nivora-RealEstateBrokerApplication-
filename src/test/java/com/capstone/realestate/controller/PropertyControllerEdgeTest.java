package com.capstone.realestate.controller;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.entity.User;
import com.capstone.realestate.repository.BrokerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.service.IPropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyControllerEdgeTest {

    @Mock private IPropertyService propertyService;
    @Mock private UserRepository userRepository;
    @Mock private BrokerRepository brokerRepository;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private PropertyController propertyController;

    @Test
    void addProperty_WhenAuthenticatedUserMissing_ShouldThrow() {
        when(userDetails.getUsername()).thenReturn("missing@example.com");
        when(userRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> propertyController.addProperty(new Property(), userDetails));
    }

    @Test
    void editProperty_WhenBrokerProfileMissing_ShouldThrow() {
        User u = new User();
        u.setUserId(77);

        when(userDetails.getUsername()).thenReturn("broker@example.com");
        when(userRepository.findByEmailIgnoreCase("broker@example.com")).thenReturn(Optional.of(u));
        when(brokerRepository.findByUser_UserId(77)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> propertyController.editProperty(10, new Property(), userDetails));
    }

    @Test
    void myListings_WhenBrokerResolved_ShouldExecute() {
        User u = new User();
        u.setUserId(9);
        Broker b = new Broker();
        b.setBroId(99);

        when(userDetails.getUsername()).thenReturn("broker@example.com");
        when(userRepository.findByEmailIgnoreCase("broker@example.com")).thenReturn(Optional.of(u));
        when(brokerRepository.findByUser_UserId(9)).thenReturn(Optional.of(b));

        propertyController.myListings(userDetails);
    }
}
