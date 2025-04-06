package com.TrainMS.Train.controllers;

import com.TrainMS.Train.dtos.IdRequest;
import com.TrainMS.Train.models.Station;
import com.TrainMS.Train.services.StationService;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/station")
public class StationController {

    private final StationService stationService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Station Controller!";
    }

    @GetMapping("/all")
    public ResponseEntity<List<Station>> getAllStations() {
        return new ResponseEntity<>(stationService.getAllStations(), HttpStatus.OK);
    }

    @GetMapping("/byId")
    public ResponseEntity<Station> getStationById(@RequestBody Long id) {
        Station station = stationService.getStationById(id);
        if (station == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(station, HttpStatus.OK);
    }

    @GetMapping("/byName")
    public ResponseEntity<Station> getStationByName(@RequestBody String stationName) {
        Station station = stationService.getStationByName(stationName);
        if (station == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(station, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addStation(@RequestBody Station station) {
        stationService.AddStation(station);
        return new ResponseEntity<>("Station added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteStation(@RequestBody IdRequest idRequest) {
        Long id = idRequest.getId();
        if (stationService.deleteStation(id)) {
            return new ResponseEntity<>("Station deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Station not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<String> updateStation(@RequestBody Station updatedStation) {
        Long id = updatedStation.getStationID();
        if (stationService.updateStation(id, updatedStation)) {
            return new ResponseEntity<>("Station updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Station not found", HttpStatus.NOT_FOUND);
        }
    }


    
}
