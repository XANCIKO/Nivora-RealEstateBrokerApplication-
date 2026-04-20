package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.BrokerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerServiceImplTest {

    @Mock
    private BrokerRepository brokerRepository;

    @InjectMocks
    private BrokerServiceImpl brokerService;

    @Test
    void addBroker_ShouldSave() {
        Broker broker = new Broker();
        broker.setBroName("A");
        when(brokerRepository.save(broker)).thenReturn(broker);

        Broker out = brokerService.addBroker(broker);

        assertEquals("A", out.getBroName());
        verify(brokerRepository, times(1)).save(broker);
    }

    @Test
    void editBroker_WhenMissing_ShouldThrow() {
        Broker broker = new Broker();
        broker.setBroId(10);
        when(brokerRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> brokerService.editBroker(broker));
    }

    @Test
    void removeBroker_WhenExists_ShouldDeleteAndReturn() {
        Broker broker = new Broker();
        broker.setBroId(5);
        when(brokerRepository.findById(5)).thenReturn(Optional.of(broker));

        Broker out = brokerService.removeBroker(5);

        assertEquals(5, out.getBroId());
        verify(brokerRepository, times(1)).delete(broker);
    }

    @Test
    void viewBroker_WhenMissing_ShouldThrow() {
        when(brokerRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> brokerService.viewBroker(99));
    }

    @Test
    void listAllBrokers_ShouldReturnAll() {
        when(brokerRepository.findAll()).thenReturn(List.of(new Broker(), new Broker()));

        List<Broker> out = brokerService.listAllBrokers();

        assertEquals(2, out.size());
    }
}
