package com.TrainMS.Train.services;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.TrainMS.Train.models.Station;
import com.TrainMS.Train.repositories.StationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }   

    public Station getStationById(Long id) {
        return stationRepository.findById(id).orElse(null);
    }


    public Station getStationByName(String stationName) {
        return stationRepository.findByStationName(stationName).orElse(null);
    }

    public void AddStation(Station station) {
        stationRepository.save(station);
    }


    public boolean deleteStation(Long id) {
        try {
            stationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateStation(Long id, Station updatedStation) {
        
        Optional<Station> existingStation = stationRepository.findById(id);
        if (existingStation.isPresent()) {
            Station station = existingStation.get();
            station.setStationName(updatedStation.getStationName());
            stationRepository.save(station);
            return true;
        }
        return false;
    }


}
