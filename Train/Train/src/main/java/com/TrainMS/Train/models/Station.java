package com.TrainMS.Train.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
// @Table(name = "station")
@Data
@NoArgsConstructor
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationID;
    
    
    @Column(name = "station_name", nullable = false, unique = true)
    private String stationName;

   
}
