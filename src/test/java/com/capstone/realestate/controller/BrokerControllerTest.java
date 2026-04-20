package com.capstone.realestate.controller;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.service.IBrokerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerControllerTest {

    @Mock
    private IBrokerService brokerService;

    @InjectMocks
    private BrokerController brokerController;

    @Test
    void listAll_ShouldReturnResponse() {
        when(brokerService.listAllBrokers()).thenReturn(List.of(new Broker()));

        var response = brokerController.listAll();

        assertNotNull(response);
        verify(brokerService, times(1)).listAllBrokers();
    }

    @Test
    void viewBroker_ShouldDelegateToService() {
        when(brokerService.viewBroker(7)).thenReturn(new Broker());

        var response = brokerController.viewBroker(7);

        assertNotNull(response);
        verify(brokerService, times(1)).viewBroker(7);
    }

    @Test
    void editBroker_ShouldSetPathIdOnPayload() {
        Broker payload = new Broker();

        brokerController.editBroker(11, payload);

        ArgumentCaptor<Broker> captor = ArgumentCaptor.forClass(Broker.class);
        verify(brokerService, times(1)).editBroker(captor.capture());
        assertEquals(11, captor.getValue().getBroId());
    }

    @Test
    void removeBroker_ShouldDelegateToService() {
        when(brokerService.removeBroker(4)).thenReturn(new Broker());

        var response = brokerController.removeBroker(4);

        assertNotNull(response);
        verify(brokerService, times(1)).removeBroker(4);
    }
}
