package com.GPS_Microservice.GPS.Microservice.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.GPS_Microservice.GPS.Microservice.model.GPSLog;
import com.GPS_Microservice.GPS.Microservice.model.Vehicle;
import com.GPS_Microservice.GPS.Microservice.repository.GPSLogRepository;

@Service
public class GPSLogService {
    @Autowired
    private GPSLogRepository gpsLogRepository;

    public GPSLog save(GPSLog gpsLog) {
        if (gpsLog.getSpeed()>100) {
            gpsLog.setSpeedViolation(true);
        }
        return gpsLogRepository.save(gpsLog);
    }

    public Optional<GPSLog> getLastLocation(Vehicle vehicle) {
        return gpsLogRepository.findTopByVehicleOrderByTimestampDesc(vehicle);
    }

    public Page<GPSLog> getHistory(Vehicle vehicle, LocalDateTime from, LocalDateTime to, Pageable page) {
        return gpsLogRepository.findByVehicleAndTimestampBetweenOrderByTimestampAsc(vehicle, from, to, page);
    }
}
