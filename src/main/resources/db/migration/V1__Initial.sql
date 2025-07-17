-- V1__Initial.sql

CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    plate_number VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255)
);

CREATE TABLE gps_logs (
    id SERIAL PRIMARY KEY,
    vehicle_reference BIGINT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    speed DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    speed_violation BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_vehicle FOREIGN KEY (vehicle_reference) REFERENCES vehicles(id)
);
