package com.capstone.realestate.service;

import com.capstone.realestate.dto.PropertyCriteria;
import com.capstone.realestate.entity.Property;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPropertyService {
    Property addProperty(Property property, int broId);
    Property editProperty(int propId, Property property, int broId);
    Property addPropertyImages(int propId, int broId, List<MultipartFile> images);
    Property removeProperty(int propId);
    Property viewProperty(int propId);
    List<Property> listAllProperties();
    List<Property> listPropertyByCriteria(PropertyCriteria criteria);
    List<Property> listPropertiesByBroker(int broId);
}
