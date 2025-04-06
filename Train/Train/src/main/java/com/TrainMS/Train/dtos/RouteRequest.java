package com.TrainMS.Train.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RouteRequest {
    
    private Long trainId;
    
    private String source;
    private String destination;
    
    public LocalDateTime arrivalTime;
    public LocalDateTime departureTime;


}
