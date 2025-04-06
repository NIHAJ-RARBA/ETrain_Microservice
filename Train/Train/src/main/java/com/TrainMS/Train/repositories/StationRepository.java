package com.TrainMS.Train.repositories;

import com.TrainMS.Train.models.Station;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {

    Optional<Station> findByStationName(String stationName);

    
}