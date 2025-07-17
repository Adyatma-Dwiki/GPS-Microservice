package com.GPS_Microservice.GPS.Microservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.GPS_Microservice.GPS.Microservice.dto.GPSLogRequestDTO;
import com.GPS_Microservice.GPS.Microservice.model.GPSLog;
import com.GPS_Microservice.GPS.Microservice.model.Vehicle;
import com.GPS_Microservice.GPS.Microservice.service.GPSLogService;
import com.GPS_Microservice.GPS.Microservice.service.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class GPSController {

    @Autowired
    private GPSLogService gpsLogService;

    @Autowired
    private VehicleService vehicleService;

    @Operation(summary = "Submit new GPS log", description = "Use this endpoint to submit a new GPS log for a specific vehicle. "
            +
            "The request should include the vehicle ID, latitude, longitude, and timestamp.\n\n" +
            "**Example request:**\n" +
            "`POST /api/gps`\n\n" +
            "**Request body example:**\n" +
            "```json\n" +
            "{\n" +
            "  \"vehicleReference\": 1,\n" +
            "  \"latitude\": -6.200000,\n" +
            "  \"longitude\": 106.816666,\n" +
            "  \"timestamp\": \"2025-07-17T10:00:00\"\n" +
            "}\n" +
            "```\n\n")

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "GPS log saved successfully", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n"
                    +
                    "  \"message\": \"GPS log saved successfully\",\n" +
                    "  \"data\": {\n" +
                    "    \"vehicleReference\": 1,\n" +
                    "    \"latitude\": -6.2,\n" +
                    "    \"longitude\": 106.8,\n" +
                    "    \"speed\": 80,\n" +
                    "    \"timestamp\": \"2025-07-16T10:00:00\"\n" +
                    "  }\n" +
                    "}"))),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"message\": \"Validation failed\", \"errors\": { \"latitude\": \"must be greater than or equal to -90.0\" } }"))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"message\": \"Vehicle not found\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"message\": \"Unexpected error\" }")))
    })
    @PostMapping("/gps")
    public ResponseEntity<Map<String, Object>> saveGPSLog(@Valid @RequestBody GPSLogRequestDTO request) {
        Vehicle vehicle = vehicleService.getVehicleById(request.getVehicleReference())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        LocalDateTime timestamp = LocalDateTime.parse(request.getTimestamp());

        GPSLog gpsLog = new GPSLog();
        gpsLog.setVehicle(vehicle);
        gpsLog.setLatitude(request.getLatitude());
        gpsLog.setLongitude(request.getLongitude());
        gpsLog.setSpeed(request.getSpeed());
        gpsLog.setTimestamp(timestamp);

        gpsLogService.save(gpsLog);

        Map<String, Object> data = new HashMap<>();
        data.put("vehicleReference", vehicle.getId());
        data.put("latitude", gpsLog.getLatitude());
        data.put("longitude", gpsLog.getLongitude());
        data.put("speed", gpsLog.getSpeed());
        data.put("timestamp", gpsLog.getTimestamp());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "GPS log saved successfully");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get last known GPS location", description = "Use this endpoint to get the most recent GPS location of a specific vehicle by its ID.\n\n"
            +
            "**Example request:**\n" +
            "`GET /api/vehicles/1/last-location`\n\n" +
            "**Response includes:**\n" +
            "- `latitude`: Latest latitude value\n" +
            "- `longitude`: Latest longitude value\n" +
            "- `timestamp`: The time the location was recorded\n\n" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Last known location retrieved", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n"
                    +
                    "  \"message\": \"Last known location retrieved\",\n" +
                    "  \"data\": {\n" +
                    "    \"vehicleReference\": 1,\n" +
                    "    \"latitude\": -6.2,\n" +
                    "    \"longitude\": 106.8,\n" +
                    "    \"speed\": 80,\n" +
                    "    \"timestamp\": \"2025-07-16T10:00:00\"\n" +
                    "  }\n" +
                    "}"))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found or no GPS log found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"message\": \"No GPS log found\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"message\": \"Unexpected error\" }")))
        })
    @GetMapping("/vehicles/{id}/last-location")
    public ResponseEntity<Map<String, Object>> getLastLocation(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        return gpsLogService.getLastLocation(vehicle)
                .map(data -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Last known location retrieved");
                    response.put("data", data);
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No GPS log found"));
    }

    @Operation(summary = "Get GPS history", description = "Use this endpoint to retrieve the GPS history of a vehicle by its ID.\n\n"
            +
            "**Parameters:**\n" +
            "- `from` and `to`: Start and end timestamps (format: `yyyy-MM-dd'T'HH:mm:ss`)\n" +
            "- `page` and `size`: Optional, for pagination (default: `page=0`, `size=10`)\n\n" +
            "**Example request:**\n" +
            "`GET /api/vehicles/1/history?from=2025-07-01T00:00:00&to=2025-07-16T23:59:59&page=0&size=10`\n\n" +
            "**Response includes:**\n" +
            "- `data`: List of GPS logs\n" +
            "- `currentPage`: The current page number\n" +
            "- `totalItems`: Total number of logs\n" +
            "- `totalPages`: Total number of available pages")
            

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "GPS history retrieved", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n"
                    +
                    "  \"message\": \"GPS history retrieved\",\n" +
                    "  \"data\": [\n" +
                    "    {\n" +
                    "      \"id\": 15,\n" +
                    "      \"vehicle\": {\n" +
                    "        \"id\": 1,\n" +
                    "        \"plateNumber\": \"B1234XYZ\",\n" +
                    "        \"name\": \"Truk 1\",\n" +
                    "        \"type\": \"Truck\"\n" +
                    "      },\n" +
                    "      \"latitude\": -6.2,\n" +
                    "      \"longitude\": 106.8,\n" +
                    "      \"speed\": 200,\n" +
                    "      \"timestamp\": \"2025-07-16T10:00:00\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"currentPage\": 1,\n" +
                    "  \"totalItems\": 13,\n" +
                    "  \"totalPages\": 2\n" +
                    "}"))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"message\": \"Vehicle not found\" }"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"message\": \"Unexpected error\" }")))
    })

    @GetMapping("/vehicles/{id}/history")
    public ResponseEntity<Map<String, Object>> getHistory(
            @PathVariable Long id,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Vehicle vehicle = vehicleService.getVehicleById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        Page<GPSLog> historyPage = gpsLogService.getHistory(vehicle, from, to, PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "GPS history retrieved");
        response.put("data", historyPage.getContent());
        response.put("currentPage", historyPage.getNumber());
        response.put("totalItems", historyPage.getTotalElements());
        response.put("totalPages", historyPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
