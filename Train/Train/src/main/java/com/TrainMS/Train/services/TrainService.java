package com.TrainMS.Train.services;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.TrainMS.Train.models.Train;
import com.TrainMS.Train.repositories.TrainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;

    
    public List<Train> getTrains() {
        return trainRepository.findAll();
    }   


    public void AddTrain(Train train) {
        trainRepository.save(train);
    }

    public Train getTrain(Long id) {
        return trainRepository.findById(id).orElse(null);
    }
    
    public boolean deleteTrain(Long id) {
        try {
            trainRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateTrain(Long id, Train updatedTrain) {
        
        Optional<Train> existingTrain = trainRepository.findById(id);
        if (existingTrain.isPresent()) {
            Train train = existingTrain.get();
            train.setName(updatedTrain.getName());
            trainRepository.save(train);
            return true;
        }
        return false;
    }

}
