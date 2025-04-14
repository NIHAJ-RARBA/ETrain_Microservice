package com.TrainMS.Train.controllers;

import com.TrainMS.Train.dtos.IdRequest;
import com.TrainMS.Train.dtos.TrainRequest;
// import com.TrainMS.Train.models.Route;
import com.TrainMS.Train.models.Train;
import lombok.RequiredArgsConstructor;

import com.TrainMS.Train.services.RouteService;
import com.TrainMS.Train.services.TrainService;

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
@RequestMapping("/train")
public class TrainController {
    private final TrainService trainService;
    private final RouteService routeService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Train Controller!";
    }

    @GetMapping("/all")
    public ResponseEntity<List<Train>> getTrains() {
        
        return new ResponseEntity<>(trainService.getTrains(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Train> getTrain(@RequestBody IdRequest IdRequest) {
        Long id = IdRequest.getId();
        Train temp = trainService.getTrain(id);
        if (temp == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }



    @PostMapping
    public ResponseEntity<String> addTrain(@RequestBody Train train) {
        trainService.AddTrain(train);
        return new ResponseEntity<>("Train added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTrain(@RequestBody IdRequest IdRequest) {
        Long id = IdRequest.getId();

        if (trainService.deleteTrain(id)) {
            // Also delete the routes associated with this train
            routeService.deleteAllRoutesByTrainId(id);
            return new ResponseEntity<>("Train deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Train not found", HttpStatus.NOT_FOUND);
        }
    }
    

    @PutMapping
    public ResponseEntity<String> updateTrain(@RequestBody TrainRequest trainRequest) {
        
        Train train = new Train();
        train.setTrainID(trainRequest.getId());
        train.setName(trainRequest.getName());

        if (trainService.updateTrain(train.getTrainID(), train)) {
            return new ResponseEntity<>("Train updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Train not found", HttpStatus.NOT_FOUND);
        }
    }


}
