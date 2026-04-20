package com.capstone.realestate.controller;

import com.capstone.realestate.dto.ApiResponse;
import com.capstone.realestate.dto.BrokerContactDto;
import com.capstone.realestate.dto.PropertyCriteria;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.repository.BrokerRepository;
import com.capstone.realestate.repository.UserRepository;
import com.capstone.realestate.service.IPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final IPropertyService propertyService;
    private final UserRepository userRepository;
    private final BrokerRepository brokerRepository;

    // Public - search available properties
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Property>>> searchProperties(
            @RequestParam(required = false) String config,
            @RequestParam(required = false) String offer,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") double minCost,
            @RequestParam(defaultValue = "0") double maxCost) {

        PropertyCriteria criteria = new PropertyCriteria(config, offer, city, minCost, maxCost);
        List<Property> properties = propertyService.listPropertyByCriteria(criteria);
        return ResponseEntity.ok(ApiResponse.success("Properties fetched", properties));
    }

    // Public - list all available properties
    @GetMapping
    public ResponseEntity<ApiResponse<List<Property>>> listAll() {
        return ResponseEntity.ok(ApiResponse.success("All properties", propertyService.listAllProperties()));
    }

    // Public - view a single property
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Property>> viewProperty(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success("Property found", propertyService.viewProperty(id)));
    }

    // Public - broker contact for a property
    @GetMapping("/{id}/broker-contact")
    public ResponseEntity<ApiResponse<BrokerContactDto>> viewPropertyBrokerContact(@PathVariable int id) {
        Property property = propertyService.viewProperty(id);

        if (property.getBroker() == null) {
            return ResponseEntity.ok(ApiResponse.success("Broker contact not available", null));
        }

        var broker = brokerRepository.findById(property.getBroker().getBroId()).orElse(null);
        if (broker == null || broker.getUser() == null) {
            return ResponseEntity.ok(ApiResponse.success("Broker contact not available", null));
        }

        BrokerContactDto contact = new BrokerContactDto(
                broker.getBroId(),
                broker.getBroName(),
                broker.getUser().getEmail(),
                broker.getUser().getMobile()
        );

        return ResponseEntity.ok(ApiResponse.success("Broker contact found", contact));
    }

    // Broker - add property
    @PostMapping
    public ResponseEntity<ApiResponse<Property>> addProperty(
            @RequestBody Property property,
            @AuthenticationPrincipal UserDetails userDetails) {
        int broId = getBroId(userDetails);
        Property saved = propertyService.addProperty(property, broId);
        return ResponseEntity.ok(ApiResponse.success("Property added successfully", saved));
    }

    // Broker - update property
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Property>> editProperty(
            @PathVariable int id,
            @RequestBody Property property,
            @AuthenticationPrincipal UserDetails userDetails) {
        int broId = getBroId(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Property updated", propertyService.editProperty(id, property, broId)));
    }

    // Broker - upload property images
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Property>> uploadPropertyImages(
            @PathVariable int id,
            @RequestParam("images") List<MultipartFile> images,
            @AuthenticationPrincipal UserDetails userDetails) {
        int broId = getBroId(userDetails);
        Property updated = propertyService.addPropertyImages(id, broId, images);
        return ResponseEntity.ok(ApiResponse.success("Property images uploaded", updated));
    }

    // Broker - delete property
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Property>> deleteProperty(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.success("Property deleted", propertyService.removeProperty(id)));
    }

    // Broker - view own properties
    @GetMapping("/my-listings")
    public ResponseEntity<ApiResponse<List<Property>>> myListings(
            @AuthenticationPrincipal UserDetails userDetails) {
        int broId = getBroId(userDetails);
        return ResponseEntity.ok(ApiResponse.success("Your listings", propertyService.listPropertiesByBroker(broId)));
    }

    private int getBroId(UserDetails userDetails) {
        var user = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow();
        return brokerRepository.findByUser_UserId(user.getUserId()).orElseThrow().getBroId();
    }
}
