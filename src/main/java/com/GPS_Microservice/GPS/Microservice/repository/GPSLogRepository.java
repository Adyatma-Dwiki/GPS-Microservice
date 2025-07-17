package com.GPS_Microservice.GPS.Microservice.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GPS_Microservice.GPS.Microservice.model.GPSLog;
import com.GPS_Microservice.GPS.Microservice.model.Vehicle;


@Repository
public interface GPSLogRepository extends JpaRepository<GPSLog, Long>{
    // Ambil log terbaru untuk kendaraan
    Optional<GPSLog> findTopByVehicleOrderByTimestampDesc(Vehicle vehicle);

    // Ambil semua log bedasarkan kendaraan dalam range waktu tertentu
    Page<GPSLog> findByVehicleAndTimestampBetweenOrderByTimestampAsc(
        Vehicle vehicle, 
        LocalDateTime from, 
        LocalDateTime to, 
        Pageable page);
    
    int deleteByTimestampBefore(LocalDateTime threshold);
} 