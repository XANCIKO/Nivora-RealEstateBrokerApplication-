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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyControllerAdditionalTest {

    @Mock private IPropertyService propertyService;
    @Mock private UserRepository userRepository;
    @Mock private BrokerRepository brokerRepository;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private PropertyController propertyController;

    private void mockBrokerResolution(int userId, int broId) {
        User u = new User();
        u.setUserId(userId);
        Broker b = new Broker();
        b.setBroId(broId);
        when(userDetails.getUsername()).thenReturn("broker@example.com");
        when(userRepository.findByEmailIgnoreCase("broker@example.com")).thenReturn(Optional.of(u));
        when(brokerRepository.findByUser_UserId(userId)).thenReturn(Optional.of(b));
    }

    @Test
    void searchProperties_ShouldReturnFetchedMessage() {
        when(propertyService.listPropertyByCriteria(any())).thenReturn(List.of());

        var response = propertyController.searchProperties(null, null, null, 0, 0);

        assertEquals("Properties fetched", response.getBody().getMessage());
    }

    @Test
    void searchProperties_ShouldReturnSuccess() {
        when(propertyService.listPropertyByCriteria(any())).thenReturn(List.of(new Property()));

        var response = propertyController.searchProperties("2BHK", "SALE", "Che", 1, 9);

        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void listAll_ShouldReturnAllPropertiesMessage() {
        when(propertyService.listAllProperties()).thenReturn(List.of());

        var response = propertyController.listAll();

        assertEquals("All properties", response.getBody().getMessage());
    }

    @Test
    void viewProperty_ShouldDelegateById() {
        when(propertyService.viewProperty(3)).thenReturn(new Property());

        propertyController.viewProperty(3);

        verify(propertyService).viewProperty(3);
    }

    @Test
    void viewProperty_ShouldReturnFoundMessage() {
        when(propertyService.viewProperty(3)).thenReturn(new Property());

        var response = propertyController.viewProperty(3);

        assertEquals("Property found", response.getBody().getMessage());
    }

    @Test
    void addProperty_ShouldUseResolvedBrokerId() {
        mockBrokerResolution(1, 10);
        Property p = new Property();
        when(propertyService.addProperty(p, 10)).thenReturn(p);

        propertyController.addProperty(p, userDetails);

        verify(propertyService).addProperty(p, 10);
    }

    @Test
    void addProperty_ShouldReturnAddedMessage() {
        mockBrokerResolution(2, 20);
        Property p = new Property();
        when(propertyService.addProperty(p, 20)).thenReturn(p);

        var response = propertyController.addProperty(p, userDetails);

        assertEquals("Property added successfully", response.getBody().getMessage());
    }

    @Test
    void editProperty_ShouldPassPathId() {
        mockBrokerResolution(3, 30);
        Property p = new Property();
        when(propertyService.editProperty(8, p, 30)).thenReturn(p);

        propertyController.editProperty(8, p, userDetails);

        verify(propertyService).editProperty(8, p, 30);
    }

    @Test
    void editProperty_ShouldReturnUpdatedMessage() {
        mockBrokerResolution(3, 30);
        Property p = new Property();
        when(propertyService.editProperty(8, p, 30)).thenReturn(p);

        var response = propertyController.editProperty(8, p, userDetails);

        assertEquals("Property updated", response.getBody().getMessage());
    }

    @Test
    void deleteProperty_ShouldDelegateToService() {
        when(propertyService.removeProperty(7)).thenReturn(new Property());

        propertyController.deleteProperty(7);

        verify(propertyService).removeProperty(7);
    }

    @Test
    void deleteProperty_ShouldReturnDeletedMessage() {
        when(propertyService.removeProperty(7)).thenReturn(new Property());

        var response = propertyController.deleteProperty(7);

        assertEquals("Property deleted", response.getBody().getMessage());
    }

    @Test
    void myListings_ShouldReturnYourListingsMessage() {
        mockBrokerResolution(4, 40);
        when(propertyService.listPropertiesByBroker(40)).thenReturn(List.of(new Property()));

        var response = propertyController.myListings(userDetails);

        assertEquals("Your listings", response.getBody().getMessage());
    }
}
