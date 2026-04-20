package com.capstone.realestate.service.impl;

import com.capstone.realestate.dto.PropertyCriteria;
import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.exception.ResourceNotFoundException;
import com.capstone.realestate.repository.BrokerRepository;
import com.capstone.realestate.repository.PropertyRepository;
import com.capstone.realestate.service.IPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements IPropertyService {

    private final PropertyRepository propertyRepository;
    private final BrokerRepository brokerRepository;
    private final PropertyImageStorageService propertyImageStorageService;

    @Override
    public Property addProperty(Property property, int broId) {
        Broker broker = brokerRepository.findById(broId)
                .orElseThrow(() -> new ResourceNotFoundException("Broker not found with id: " + broId));
        validateNonNegative(property);
        property.setBroker(broker);
        property.setStatus(true);
        return propertyRepository.save(property);
    }

    @Override
    public Property editProperty(int propId, Property property, int broId) {
        Property existing = propertyRepository.findById(propId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propId));

        if (existing.getBroker() == null || existing.getBroker().getBroId() != broId) {
            throw new AccessDeniedException("You can update only your own properties");
        }

        validateNonNegative(property);

        existing.setConfiguration(property.getConfiguration());
        existing.setOfferType(property.getOfferType());
        existing.setOfferCost(property.getOfferCost());
        existing.setAreaSqft(property.getAreaSqft());
        existing.setAddress(property.getAddress());
        existing.setStreet(property.getStreet());
        existing.setCity(property.getCity());

        return propertyRepository.save(existing);
    }

    @Override
    public Property addPropertyImages(int propId, int broId, List<MultipartFile> images) {
        Property existing = propertyRepository.findById(propId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propId));

        if (existing.getBroker() == null || existing.getBroker().getBroId() != broId) {
            throw new AccessDeniedException("You can upload images only for your own properties");
        }

        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("Please upload at least one image.");
        }

        List<String> storedUrls = propertyImageStorageService.storeImages(images);

        List<String> merged = new ArrayList<>();
        if (existing.getImageUrls() != null) {
            merged.addAll(existing.getImageUrls());
        }
        merged.addAll(storedUrls);

        existing.setImageUrls(new ArrayList<>(new LinkedHashSet<>(merged)));
        return propertyRepository.save(existing);
    }

    @Override
    public Property removeProperty(int propId) {
        Property property = propertyRepository.findById(propId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propId));
        propertyRepository.delete(property);
        return property;
    }

    @Override
    public Property viewProperty(int propId) {
        return propertyRepository.findById(propId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propId));
    }

    @Override
    public List<Property> listAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public List<Property> listPropertyByCriteria(PropertyCriteria criteria) {
        return propertyRepository.findByCriteria(
                criteria.getConfig(),
                criteria.getOffer(),
                criteria.getCity(),
                criteria.getMinCost(),
                criteria.getMaxCost()
        );
    }

    @Override
    public List<Property> listPropertiesByBroker(int broId) {
        return propertyRepository.findByBroker_BroId(broId);
    }

    private void validateNonNegative(Property property) {
        if (property.getOfferCost() < 0 || property.getAreaSqft() < 0) {
            throw new IllegalArgumentException("Price/Cost and Area cannot be negative.");
        }
    }
}
