package com.TrainMS.Train.repositories;

import com.TrainMS.Train.models.Station;
import com.TrainMS.Train.models.Train;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TrainMS.Train.models.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findBySourceAndDestination(Station source, Station destination);

    List<Route> findBySource(Station source);

    List<Route> findByDestination(Station destination);

    
    List<Route> findByTrain(Train train);

    boolean deleteByTrain(Train train);



}