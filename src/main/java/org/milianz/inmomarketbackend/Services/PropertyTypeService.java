package org.milianz.inmomarketbackend.Services;

import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationSaveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationUpdateDTO;
import org.milianz.inmomarketbackend.Domain.Entities.PropertyType;
import org.milianz.inmomarketbackend.Domain.Repositories.iPropertyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PropertyTypeService {

    @Autowired
    private iPropertyTypeRepository propertyTypeRepository;

    public PropertyType createPropertyType(PublicationSaveDTO publicationSaveDTO) {

        return propertyTypeRepository.findByTypeName(publicationSaveDTO.getTypeName()).orElseGet(() -> {;
            PropertyType newPropertyType = new PropertyType();
            newPropertyType.setTypeName(publicationSaveDTO.getTypeName());
            return propertyTypeRepository.save(newPropertyType);
        });
    }

    // Sobrecarga del método para soportar actualización de publicaciones
    public PropertyType createPropertyType(PublicationUpdateDTO publicationUpdateDTO) {

        return propertyTypeRepository.findByTypeName(publicationUpdateDTO.getTypeName()).orElseGet(() -> {;
            PropertyType newPropertyType = new PropertyType();
            newPropertyType.setTypeName(publicationUpdateDTO.getTypeName());
            return propertyTypeRepository.save(newPropertyType);
        });
    }
}
