package com.capstone.realestate.controller;

import com.capstone.realestate.dto.ApiResponse;
import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.service.IBrokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brokers")
@RequiredArgsConstructor
public class BrokerController {

    private final IBrokerService brokerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Broker>>> listAll() {
        return ResponseEntity.ok(ApiResponse.success("All brokers", brokerService.listAllBrokers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Broker>> viewBroker(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success("Broker found", brokerService.viewBroker(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Broker>> editBroker(
            @PathVariable int id, @RequestBody Broker broker) {
        broker.setBroId(id);
        return ResponseEntity.ok(ApiResponse.success("Broker updated", brokerService.editBroker(broker)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Broker>> removeBroker(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success("Broker removed", brokerService.removeBroker(id)));
    }
}
