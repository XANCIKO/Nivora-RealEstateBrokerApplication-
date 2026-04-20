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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerControllerAdditionalTest {

    @Mock
    private IBrokerService brokerService;

    @InjectMocks
    private BrokerController brokerController;

    @Test
    void listAll_ShouldReturnSuccessTrue() {
        when(brokerService.listAllBrokers()).thenReturn(List.of(new Broker()));

        var response = brokerController.listAll();

        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void listAll_ShouldUseExpectedMessage() {
        when(brokerService.listAllBrokers()).thenReturn(List.of());

        var response = brokerController.listAll();

        assertEquals("All brokers", response.getBody().getMessage());
    }

    @Test
    void viewBroker_ShouldReturnPayloadFromService() {
        Broker broker = new Broker();
        broker.setBroId(5);
        when(brokerService.viewBroker(5)).thenReturn(broker);

        var response = brokerController.viewBroker(5);

        assertEquals(5, response.getBody().getData().getBroId());
    }

    @Test
    void viewBroker_ShouldDelegateOnce() {
        when(brokerService.viewBroker(1)).thenReturn(new Broker());

        brokerController.viewBroker(1);

        verify(brokerService, times(1)).viewBroker(1);
    }

    @Test
    void editBroker_ShouldForcePathId() {
        Broker payload = new Broker();
        payload.setBroId(99);
        when(brokerService.editBroker(any(Broker.class))).thenReturn(new Broker());

        brokerController.editBroker(12, payload);

        ArgumentCaptor<Broker> captor = ArgumentCaptor.forClass(Broker.class);
        verify(brokerService).editBroker(captor.capture());
        assertEquals(12, captor.getValue().getBroId());
    }

    @Test
    void editBroker_ShouldReturnUpdatedMessage() {
        when(brokerService.editBroker(any(Broker.class))).thenReturn(new Broker());

        var response = brokerController.editBroker(2, new Broker());

        assertEquals("Broker updated", response.getBody().getMessage());
    }

    @Test
    void removeBroker_ShouldDelegate() {
        when(brokerService.removeBroker(4)).thenReturn(new Broker());

        brokerController.removeBroker(4);

        verify(brokerService).removeBroker(4);
    }

    @Test
    void removeBroker_ShouldReturnRemovedMessage() {
        when(brokerService.removeBroker(6)).thenReturn(new Broker());

        var response = brokerController.removeBroker(6);

        assertEquals("Broker removed", response.getBody().getMessage());
    }
}
