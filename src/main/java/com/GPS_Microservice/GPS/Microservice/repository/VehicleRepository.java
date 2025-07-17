package com.GPS_Microservice.GPS.Microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GPS_Microservice.GPS.Microservice.model.Vehicle;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}