package com.GPS_Microservice.GPS.Microservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GPS_Microservice.GPS.Microservice.model.Vehicle;
import com.GPS_Microservice.GPS.Microservice.repository.VehicleRepository;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public Optional<Vehicle> getVehicleById(Long id){
        return vehicleRepository.findById(id);
    }
}
