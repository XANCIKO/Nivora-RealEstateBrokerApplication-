package com.capstone.realestate.service;

import com.capstone.realestate.entity.Broker;

import java.util.List;

public interface IBrokerService {
    Broker addBroker(Broker broker);
    Broker editBroker(Broker broker);
    Broker removeBroker(int broId);
    Broker viewBroker(int broId);
    List<Broker> listAllBrokers();
}
