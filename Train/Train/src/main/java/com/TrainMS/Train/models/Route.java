package com.TrainMS.Train.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
// @Table(name = "route") // Ensure the correct table name is set
@Data
@NoArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @ManyToOne
    private Train train;

    // private Long trainId = train != null ? train.getTrainID() : null;

    @ManyToOne
    // @JoinColumn(name = "source_station", nullable = false) // Different column name
    private Station source;

    @ManyToOne
    // @JoinColumn(name = "destination_station", nullable = false) // Different column name
    private Station destination;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    public Long getSourceId() {
        return source != null ? source.getStationID() : null;
    }
    public Long getDestinationId() {
        return destination != null ? destination.getStationID() : null;
    }

    public Long getTrainId() {
        return train != null ? train.getTrainID() : null;
    }
}
