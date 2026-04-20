package com.capstone.realestate.service;

import com.capstone.realestate.entity.Deal;

import java.util.List;

public interface IDealService {
    Deal addDeal(int propId, int custId);
    List<Deal> listAllDeals();
    List<Deal> listDealsByCustomer(int custId);
}
