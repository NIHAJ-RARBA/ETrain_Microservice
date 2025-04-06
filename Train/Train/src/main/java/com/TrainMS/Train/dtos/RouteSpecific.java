package com.TrainMS.Train.dtos;

import lombok.Data;

@Data
public class RouteSpecific {
    
    private Long trainId;
    
    private String source;
    private String destination;
}