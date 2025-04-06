package com.TrainMS.Train.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.TrainMS.Train.models.Route;
import com.TrainMS.Train.models.Station;
import com.TrainMS.Train.models.Train;
import com.TrainMS.Train.repositories.RouteRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

enum Infinity {

    MINUS_INF(LocalDateTime.of(1900, 1, 1, 0, 0)),
    PLUS_INF(LocalDateTime.of(3000, 1, 1, 0, 0));
    

    private LocalDateTime value;

    Infinity(LocalDateTime value) {
        this.value = value;
    }

    public LocalDateTime getValue() {
        return value;
    }
}

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final TrainService trainService;

    public void addRoute(Route route) {
        if (route.getSourceId() == null || route.getDestinationId() == null) {
            throw new IllegalArgumentException("Source and destination IDs cannot be null");            
        }

        // Set default values if they are null
        if (route.getArrivalTime() == null) {
            route.setArrivalTime(Infinity.MINUS_INF.getValue());
        }
        if (route.getDepartureTime() == null) {
            route.setDepartureTime(Infinity.PLUS_INF.getValue());
        }

        routeRepository.save(route);
    }


        
    public List<Route> getAllRoutes() {
        
        List<Route> routes = routeRepository.findAll();
        return routes.stream()
                .sorted(Comparator.comparing(Route::getTrainId)
                        .thenComparing(Route::getArrivalTime))
                .filter(route -> route.getSourceId() != null && route.getDestinationId() != null)
                .toList();
    }   



    public Route getRouteById(Long id) {
        return routeRepository.findById(id).orElse(null);
    }

    public List<Route> getSpecificRoute(Long trainId, Long source, Long destination) {
        
        Train train = trainService.getTrain(trainId);
        if (train == null) {
            return List.of(); // Train not found, return empty list
        }
        List<Route> routes = routeRepository.findByTrain(train);
        return routes.stream()
                .filter(route -> route.getSourceId().equals(source) && route.getDestinationId().equals(destination))
                .sorted(Comparator.comparing(Route::getArrivalTime))
                .toList();
    }




    public List<Route> getRoutesBySourceAndDestination(Station source, Station destination) {
        return routeRepository.findBySourceAndDestination(source, destination).stream()
                .sorted(Comparator.comparing(Route::getArrivalTime))
                .toList();
    }

    public List<Route> getRoutesBySource(Station source) {
        return routeRepository.findBySource(source).stream()
                .sorted(Comparator.comparing(Route::getArrivalTime))
                .toList();
    }

    public List<Route> getRoutesByDestination(Station destination) {
        return routeRepository.findByDestination(destination).stream()
                .sorted(Comparator.comparing(Route::getArrivalTime))
                .toList();
    }

    public List<Route> getRoutesByTrainId(Long trainId) {
        Train train = trainService.getTrain(trainId);
        if (train == null) {
            return List.of(); // Train not found, return empty list
        }
        return routeRepository.findByTrain(train).stream()
                .sorted(Comparator.comparing(Route::getArrivalTime))
                .toList();
    }



    
    public boolean deleteRoute(Long id) {
        try {
            routeRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAllRoutesByTrainId(Long trainId) {

        Train train = trainService.getTrain(trainId);
        if (train == null) {
            return false; // Train not found
        }
        List<Route> routes = routeRepository.findByTrain(train);
        if (routes.isEmpty()) {
            return false; // No routes found for the given trainId
        }

        try{
            for (Route route : routes) {
                routeRepository.delete(route);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean deleteSpecificRoute(Long trainId, Long sourceId, Long destinationId) {
        
        List<Route> routes = this.getSpecificRoute(trainId, sourceId, destinationId);
        if (routes.isEmpty()) {
            return false; // No routes found for the given trainId
        }

        try {
            for (Route route : routes) {
                routeRepository.delete(route);
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    public boolean updateRoute(Long id, Route updatedRoute) {
        
        Optional<Route> existingRoute = routeRepository.findById(id);
        if (existingRoute.isPresent()) {
            Route route = existingRoute.get();
            route.setSource(updatedRoute.getSource());
            route.setDestination(updatedRoute.getDestination());
            route.setArrivalTime(updatedRoute.getArrivalTime());
            route.setDepartureTime(updatedRoute.getDepartureTime());
            route.setTrain(updatedRoute.getTrain());
            routeRepository.save(route);
            return true;
        }
        return false;
    }

    public boolean updateRouteByTrainId(Long trainId, Route updatedRoute) {

        if (updatedRoute.getSourceId() == null || updatedRoute.getDestinationId() == null) {
            throw new IllegalArgumentException("Source and destination IDs cannot be null");            
        }

        // Set default values if they are null
        if (updatedRoute.getArrivalTime() == null) {
            updatedRoute.setArrivalTime(Infinity.MINUS_INF.getValue());
        }
        if (updatedRoute.getDepartureTime() == null) {
            updatedRoute.setDepartureTime(Infinity.PLUS_INF.getValue());
        }

        Train train = trainService.getTrain(trainId);
        if (train == null) {
            return false; // Train not found
        }
        List<Route> existingRoutes = routeRepository.findByTrain(train);
        if (existingRoutes.isEmpty()) {
            // No routes found for the given trainId
            // Add it to the list of routes

            this.addRoute(updatedRoute);
            return true;
        }

        existingRoutes.stream().filter(route -> route.getRouteId().equals(updatedRoute.getRouteId()))
                .findFirst()
                .ifPresent(route -> {
                    route.setSource(updatedRoute.getSource());
                    route.setDestination(updatedRoute.getDestination());
                    route.setArrivalTime(updatedRoute.getArrivalTime());
                    route.setDepartureTime(updatedRoute.getDepartureTime());
                    route.setTrain(updatedRoute.getTrain());
                });
        // Save the updated routes back to the repository
        routeRepository.saveAll(existingRoutes);
        return true;

        
        
    }




}