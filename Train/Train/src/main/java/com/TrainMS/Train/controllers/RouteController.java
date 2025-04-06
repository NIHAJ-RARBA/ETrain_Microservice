package com.TrainMS.Train.controllers;

import com.TrainMS.Train.dtos.RouteRequest;
import com.TrainMS.Train.dtos.RouteSpecific;
import com.TrainMS.Train.dtos.RouteSrcDestRequest;
import com.TrainMS.Train.dtos.IdRequest;

import com.TrainMS.Train.models.Route;
import com.TrainMS.Train.models.Station;
import com.TrainMS.Train.models.Train;
import com.TrainMS.Train.services.RouteService;
import com.TrainMS.Train.services.StationService;
import com.TrainMS.Train.services.TrainService;

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
// import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/routes")
public class RouteController {
    private final RouteService routeService;
    private final StationService stationService;
    private final TrainService trainService;
    

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Route Controller!";
    }

    @GetMapping("/all")
    public ResponseEntity<List<Route>> getAllRoutes() {
        return new ResponseEntity<>(routeService.getAllRoutes(), HttpStatus.OK);
    }

    @GetMapping("/byTrain")
    public ResponseEntity<List<Route>> getRouteByTrain(@RequestBody IdRequest IdRequest) {
        Long id = IdRequest.getId();
        List<Route> temp = routeService.getRoutesByTrainId(id);
        if (temp == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }

    @GetMapping("/bySourceAndDestination")
    public ResponseEntity<List<Route>> getRouteBySourceAndDestination(@RequestBody RouteSrcDestRequest routeSrcDestRequest) {   
        Station source = stationService.getStationByName(routeSrcDestRequest.getSource());
        Station destination = stationService.getStationByName(routeSrcDestRequest.getDestination());
        List<Route> temp = routeService.getRoutesBySourceAndDestination(source, destination);
        if (temp == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }

    @GetMapping("/specific")
    public ResponseEntity<List<Route>> getSpecifiedRoute(@RequestBody RouteSpecific routeRequest) {
        
        Station source = stationService.getStationByName(routeRequest.getSource());
        Station destination = stationService.getStationByName(routeRequest.getDestination());
        Train train = trainService.getTrain(routeRequest.getTrainId());

        if (train == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (source == null || destination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Route> temp = routeService.getSpecificRoute(train.getTrainID(), source.getStationID(), destination.getStationID());
        if (temp == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }
    


    @PostMapping
    public ResponseEntity<String> addRoute(@RequestBody RouteRequest routeRequest) {
        
        Station source = stationService.getStationByName(routeRequest.getSource());
        Station destination = stationService.getStationByName(routeRequest.getDestination());
        Train train = trainService.getTrain(routeRequest.getTrainId());
        if (train == null) {
            return new ResponseEntity<>("Train not found", HttpStatus.NOT_FOUND);
        }

        if (source == null || destination == null) {
            return new ResponseEntity<>("Source or destination station not found", HttpStatus.NOT_FOUND);
        }

        Route route = new Route();
        route.setTrain(train);
        route.setSource(source);
        route.setDestination(destination);
        route.setArrivalTime(routeRequest.getArrivalTime());
        route.setDepartureTime(routeRequest.getDepartureTime());
        routeService.addRoute(route);


        return new ResponseEntity<>("Route added successfully", HttpStatus.CREATED);
    }


    @DeleteMapping
    public ResponseEntity<String> deleteRoute(@RequestBody IdRequest idRequest) {
        Long id = idRequest.getId();
        if (routeService.deleteRoute(id)) {
            return new ResponseEntity<>("Route deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Route not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/byTrain")
    public ResponseEntity<String> deleteAllRoutesForTrain(@RequestBody IdRequest IdRequest) {
        Long id = IdRequest.getId();
        if (routeService.deleteAllRoutesByTrainId(id)) {
            return new ResponseEntity<>("Route deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Route not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/bySourceAndDestination")
    public ResponseEntity<String> deleteRouteBySrcAndDest(@RequestBody RouteSrcDestRequest routeIdRequest) {
        
        Station source = stationService.getStationByName(routeIdRequest.getSource());
        Station destination = stationService.getStationByName(routeIdRequest.getDestination());
        if (source == null || destination == null) {
            return new ResponseEntity<>("Source or destination station not found", HttpStatus.NOT_FOUND);
        }


        if (!routeService.getRoutesBySourceAndDestination(source, destination).isEmpty()) {
            return new ResponseEntity<>("Route deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Route not found", HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/specific")
    public ResponseEntity<String> deleteSpecificRoute(@RequestBody RouteSpecific routeRequest) {

        Train train = trainService.getTrain(routeRequest.getTrainId());

        Station source = stationService.getStationByName(routeRequest.getSource());
        Station destination = stationService.getStationByName(routeRequest.getDestination());
        if (source == null || destination == null) {
            return new ResponseEntity<>("Source or destination station not found", HttpStatus.NOT_FOUND);
        }

        if (train == null) {
            return new ResponseEntity<>("Train not found", HttpStatus.NOT_FOUND);
        }

        if (routeService.deleteSpecificRoute(train.getTrainID(), source.getStationID(), destination.getStationID())) {
            return new ResponseEntity<>("Route deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Route not found", HttpStatus.NOT_FOUND);
        }

        
    }


    @PutMapping
    public ResponseEntity<String> updateRoute(@RequestBody RouteRequest routeRequest) {
        
        Station source = stationService.getStationByName(routeRequest.getSource());
        Station destination = stationService.getStationByName(routeRequest.getDestination());
        Train train = trainService.getTrain(routeRequest.getTrainId());

        if (train == null) {
            return new ResponseEntity<>("Train not found", HttpStatus.NOT_FOUND);
        }

        if (source == null || destination == null) {
            return new ResponseEntity<>("Source or destination station not found", HttpStatus.NOT_FOUND);
        }

        Route route = routeService.getSpecificRoute(train.getTrainID(), source.getStationID(), destination.getStationID()).get(0);
                if (route == null) {
            return new ResponseEntity<>("Route not found", HttpStatus.NOT_FOUND);
        }

        route.setTrain(train);
        route.setSource(source);
        route.setDestination(destination);
        route.setArrivalTime(routeRequest.getArrivalTime());
        route.setDepartureTime(routeRequest.getDepartureTime());
        

        if (routeService.updateRoute(route.getRouteId(), route)) {
            return new ResponseEntity<>("Route updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Route not found", HttpStatus.NOT_FOUND);
        }
    }



}