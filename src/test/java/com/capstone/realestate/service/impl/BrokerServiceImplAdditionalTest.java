package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.BrokerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerServiceImplAdditionalTest {

    @Mock
    private BrokerRepository brokerRepository;

    @InjectMocks
    private BrokerServiceImpl brokerService;

    @Test
    void addBroker_ShouldReturnSavedEntity() {
        Broker b = new Broker();
        b.setBroName("B");
        when(brokerRepository.save(b)).thenReturn(b);

        Broker out = brokerService.addBroker(b);

        assertEquals("B", out.getBroName());
    }

    @Test
    void addBroker_ShouldCallSaveOnce() {
        Broker b = new Broker();
        when(brokerRepository.save(b)).thenReturn(b);

        brokerService.addBroker(b);

        verify(brokerRepository, times(1)).save(b);
    }

    @Test
    void editBroker_ShouldThrowWhenNotFound() {
        Broker b = new Broker();
        b.setBroId(3);
        when(brokerRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> brokerService.editBroker(b));
    }

    @Test
    void editBroker_ShouldSaveWhenFound() {
        Broker b = new Broker();
        b.setBroId(3);
        when(brokerRepository.findById(3)).thenReturn(Optional.of(b));
        when(brokerRepository.save(b)).thenReturn(b);

        Broker out = brokerService.editBroker(b);

        assertEquals(3, out.getBroId());
    }

    @Test
    void removeBroker_ShouldThrowWhenMissing() {
        when(brokerRepository.findById(9)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> brokerService.removeBroker(9));
    }

    @Test
    void removeBroker_ShouldDeleteWhenFound() {
        Broker b = new Broker();
        b.setBroId(4);
        when(brokerRepository.findById(4)).thenReturn(Optional.of(b));

        brokerService.removeBroker(4);

        verify(brokerRepository).delete(b);
    }

    @Test
    void viewBroker_ShouldReturnEntity() {
        Broker b = new Broker();
        b.setBroId(8);
        when(brokerRepository.findById(8)).thenReturn(Optional.of(b));

        Broker out = brokerService.viewBroker(8);

        assertEquals(8, out.getBroId());
    }

    @Test
    void listAllBrokers_ShouldDelegateFindAll() {
        when(brokerRepository.findAll()).thenReturn(java.util.List.of(new Broker()));

        var out = brokerService.listAllBrokers();

        assertEquals(1, out.size());
    }
}
