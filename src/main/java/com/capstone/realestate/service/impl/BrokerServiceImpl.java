package com.capstone.realestate.service.impl;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.BrokerRepository;
import com.capstone.realestate.service.IBrokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrokerServiceImpl implements IBrokerService {

    private final BrokerRepository brokerRepository;

    @Override
    public Broker addBroker(Broker broker) {
        return brokerRepository.save(broker);
    }

    @Override
    public Broker editBroker(Broker broker) {
        brokerRepository.findById(broker.getBroId())
                .orElseThrow(() -> new ResourceNotFoundException("Broker not found with id: " + broker.getBroId()));
        return brokerRepository.save(broker);
    }

    @Override
    public Broker removeBroker(int broId) {
        Broker broker = brokerRepository.findById(broId)
                .orElseThrow(() -> new ResourceNotFoundException("Broker not found with id: " + broId));
        brokerRepository.delete(broker);
        return broker;
    }

    @Override
    public Broker viewBroker(int broId) {
        return brokerRepository.findById(broId)
                .orElseThrow(() -> new ResourceNotFoundException("Broker not found with id: " + broId));
    }

    @Override
    public List<Broker> listAllBrokers() {
        return brokerRepository.findAll();
    }
}
