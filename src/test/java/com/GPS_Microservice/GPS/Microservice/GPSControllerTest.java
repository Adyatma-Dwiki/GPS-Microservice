package com.GPS_Microservice.GPS.Microservice;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;

import com.GPS_Microservice.GPS.Microservice.controller.GPSController;
import com.GPS_Microservice.GPS.Microservice.dto.GPSLogRequestDTO;
import com.GPS_Microservice.GPS.Microservice.model.GPSLog;
import com.GPS_Microservice.GPS.Microservice.model.Vehicle;
import com.GPS_Microservice.GPS.Microservice.service.GPSLogService;
import com.GPS_Microservice.GPS.Microservice.service.VehicleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class GPSControllerTest {

    @Mock
    private GPSLogService gpsLogService;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private GPSController gpsController;

    private Vehicle mockVehicle;
    private GPSLog mockLog;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockVehicle = new Vehicle();
        mockVehicle.setId(1L);
        mockVehicle.setName("Truck 1");
        mockVehicle.setType("Truck");
        mockVehicle.setPlateNumber("B1234XYZ");

        mockLog = new GPSLog();
        mockLog.setVehicle(mockVehicle);
        mockLog.setLatitude(-6.2);
        mockLog.setLongitude(106.8);
        mockLog.setSpeed(80);
        mockLog.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testSaveGPSLog_Success() {
        GPSLogRequestDTO request = new GPSLogRequestDTO();
        request.setVehicleReference(1L);
        request.setLatitude(-6.2);
        request.setLongitude(106.8);
        request.setSpeed(80);
        request.setTimestamp(LocalDateTime.now().toString());

        when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(mockVehicle));

        ResponseEntity<Map<String, Object>> response = gpsController.saveGPSLog(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("GPS log saved successfully", response.getBody().get("message"));
    }

    @Test
    void testGetLastLocation_Success() {
        GPSLog lastLocationLog = new GPSLog();
        lastLocationLog.setVehicle(mockVehicle);
        lastLocationLog.setLatitude(-6.2);
        lastLocationLog.setLongitude(106.8);
        lastLocationLog.setSpeed(80);
        lastLocationLog.setTimestamp(LocalDateTime.now());

        when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(mockVehicle));
        when(gpsLogService.getLastLocation(mockVehicle)).thenReturn(Optional.of(lastLocationLog));

        ResponseEntity<Map<String, Object>> response = gpsController.getLastLocation(1L);

        assertEquals(200,  response.getStatusCode().value());
        assertEquals("Last known location retrieved", response.getBody().get("message"));
    }

    @Test
    void testGetHistory_Success() {
        List<GPSLog> logs = Collections.singletonList(mockLog);
        Page<GPSLog> page = new PageImpl<>(logs);

        when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(mockVehicle));
        when(gpsLogService.getHistory(any(), any(), any(), any())).thenReturn(page);

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        ResponseEntity<Map<String, Object>> response = gpsController.getHistory(1L, from, to, 0, 10);

        assertEquals(200,  response.getStatusCode().value());
        assertEquals("GPS history retrieved", response.getBody().get("message"));
        assertEquals(1, ((List<?>) response.getBody().get("data")).size());
    }

    @Test
    void testVehicleNotFound_ShouldThrowException() {
        when(vehicleService.getVehicleById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            gpsController.getLastLocation(99L);
        });

        assertEquals("404 NOT_FOUND \"Vehicle not found\"", ex.getMessage());
    }
} 
