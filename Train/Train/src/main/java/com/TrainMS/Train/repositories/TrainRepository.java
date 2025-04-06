package com.TrainMS.Train.repositories;

import com.TrainMS.Train.models.Train;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepository extends JpaRepository<Train, Long> {}