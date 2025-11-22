package org.milianz.inmomarketbackend.Services;

import org.milianz.inmomarketbackend.Domain.Entities.AvailableTime;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.AvailableTimesSaveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationSaveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationUpdateDTO;
import org.milianz.inmomarketbackend.Domain.Entities.Publication;
import org.milianz.inmomarketbackend.Domain.Repositories.iAvailableTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailableTimeService {

    @Autowired
    iAvailableTimeRepository availableTimeRepository;

    public List<AvailableTime> createAvailableTime(PublicationSaveDTO publicationSaveDTO, Publication publication) {
        List<AvailableTimesSaveDTO> availableTimes = publicationSaveDTO.getAvailableTimes();
        List<AvailableTime> availableTimesList = new ArrayList<>();

        if (availableTimes != null && !availableTimes.isEmpty()) {
            for (AvailableTimesSaveDTO availableTime : availableTimes) {
                AvailableTime availableTimeEntity = new AvailableTime();
                availableTimeEntity.setDayOfWeek(availableTime.getDayOfWeek());
                availableTimeEntity.setStartTime(availableTime.getStartTime());
                availableTimeEntity.setEndTime(availableTime.getEndTime());
                availableTimeEntity.setPublication(publication);
                availableTimesList.add(availableTimeEntity);
            }
        }

        return availableTimeRepository.saveAll(availableTimesList);
    }

    // Sobrecarga del método para soportar actualización de publicaciones
    public List<AvailableTime> createAvailableTime(PublicationUpdateDTO publicationUpdateDTO, Publication publication) {
        List<AvailableTimesSaveDTO> availableTimes = publicationUpdateDTO.getAvailableTimes();
        List<AvailableTime> availableTimesList = new ArrayList<>();

        if (availableTimes != null && !availableTimes.isEmpty()) {
            for (AvailableTimesSaveDTO availableTime : availableTimes) {
                AvailableTime availableTimeEntity = new AvailableTime();
                availableTimeEntity.setDayOfWeek(availableTime.getDayOfWeek());
                availableTimeEntity.setStartTime(availableTime.getStartTime());
                availableTimeEntity.setEndTime(availableTime.getEndTime());
                availableTimeEntity.setPublication(publication);
                availableTimesList.add(availableTimeEntity);
            }
        }

        return availableTimeRepository.saveAll(availableTimesList);
    }
}
