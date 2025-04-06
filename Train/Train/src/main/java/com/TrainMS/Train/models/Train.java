package com.TrainMS.Train.models;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
// @Table(name = "Train")
public class Train {
    
    @Id
    @Column(name = "train_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainID;
    
    private String name;
    
    // @OneToMany(mappedBy = "train")
    // private List<Route> routes;

    
    

}
