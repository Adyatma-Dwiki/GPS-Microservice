package com.GPS_Microservice.GPS.Microservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class GPSLogRequestDTO {

    @NotNull
    @Schema(example = "1", description = "ID kendaraan yang mengirimkan log GPS")
    private Long vehicleReference;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Schema(example = "-6.2", description = "Latitude lokasi kendaraan (-90 hingga +90)")
    private double latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Schema(example = "106.8", description = "Longitude lokasi kendaraan (-180 hingga +180)")
    private double longitude;

    @PositiveOrZero
    @Schema(example = "80", description = "Kecepatan kendaraan dalam km/h")
    private double speed;

    @NotBlank
    @Schema(example = "2025-07-16T10:00:00", description = "Waktu log dikirimkan (format ISO-8601)")
    private String timestamp;
}
