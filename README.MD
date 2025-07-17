# GPSLogs Microservice

## Description

This microservice handles the storage and management of GPS logs for vehicles.

## Features

- Submit GPS log
- Get logs by vehicle reference or id
- Scheduled clean-up logs (retention period is configurable)
- Swagger documentation

## Tech Stack

- Java 17
- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL
- Lombok
- Swagger

## Assumptions and Design Decisions

- **Vehicle Reference via `id` instead of `plate_number`**  
  It is assumed that `plate_number` may not always be unique or reliable due to administrative inconsistencies or potential input errors (e.g., fake or duplicated plates). Therefore, the `id` field from the `vehicle` table is used as the foreign key in the `gps_log` table to ensure uniqueness and reliable linkage.

- **Speed Stored as Raw Double**  
  The `speed` value is currently stored as a raw `double`, without conversion to km/h. This assumes that the GPS device sends consistent speed data.

- **Timestamp Represents Actual GPS Time**  
  The `timestamp` field reflects the original time reported by the GPS device rather than the time the server received the data. This approach ensures that historical tracking is chronologically accurate.

- **`@ManyToOne` Relationship between `GPSLog` and `Vehicle`**  
  A clear relational mapping is defined to prevent data redundancy and allow efficient queries for logs by vehicle. This also ensures consistency when managing or updating vehicle records.

## Database Schema

The project uses the following database schema:

- Table `vehicles`: stores vehicle information (plate number, name, type)
- Table `gps_logs`: stores GPS logs linked to vehicles via foreign key

### `vehicles` Table

| Column        | Type    | Description                          |
|---------------|---------|--------------------------------------|
| `id`          | BIGINT  | Primary key (auto-increment)         |
| `plate_number`| TEXT    | Unique license plate number          |
| `name`        | TEXT    | Vehicle name                         |
| `type`        | TEXT    | Vehicle type (optional)              |

### `gps_logs` Table

| Column            | Type         | Description                                        |
|-------------------|--------------|----------------------------------------------------|
| `id`              | BIGINT       | Primary key (auto-increment)                       |
| `vehicle_reference` | BIGINT     | Foreign key referencing `vehicles(id)`             |
| `latitude`        | DOUBLE       | GPS latitude (not null)                            |
| `longitude`       | DOUBLE       | GPS longitude (not null)                           |
| `speed`           | DOUBLE       | Vehicle speed (not null)                           |
| `timestamp`       | TIMESTAMP    | Time when the GPS data was recorded (not null)     |
| `speed_violation` | BOOLEAN      | Whether the speed exceeded the allowed limit       |


## Getting Started

### Prerequisites

- Java 17
- PostgreSQL
- Maven (or use `./mvnw`)

### Setup

1. Clone the repository:

```bash
git clone https://github.com/Adyatma-Dwiki/GPS-Microservice.git
cd GPS-Microservice

```
2. Create PostgreSQL database (e.g., fleet_gps_app) and configure connection in application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fleet_gps_app
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
```

3. Build and run the application:
```bash
./mvnw spring-boot:run
```
4. Access Swagger UI at:
```bash
http://localhost:8080/swagger-ui/index.html
```

5. Basic Auth credential
```bash
username: admin
password: admin123
```

## API Collection

- Submit GPS Log

  - **POST** `/api/gps`
  - Request Body

  ```json
  {
    "vehicleReference": 1,
    "latitude": -6.2,
    "longitude": 106.8,
    "speed": 80,
    "timestamp": "2025-07-16T10:00:00"
  }
  ```

  - Response Body (Success):

  ```json
  {
    "message": "GPS log saved successfully",
    "data": {
      "vehicleReference": 1,
      "latitude": -6.2,
      "longitude": 106.8,
      "speed": 80,
      "timestamp": "2025-07-16T10:00:00"
    }
  }
  ```

  - Response Body (Validation failed):

  ```json
  {
    "message": "Validation failed",
    "errors": {
      "latitude": "must be greater than or equal to -90.0"
    }
  }
  ```

  - Response Body (Vehicle not found):

  ```json
  {
    "message": "Vehicle not found"
  }
  ```

  - Response Body (Server error):

  ```json
  {
    "message": "Unexpected error"
  }
  ```

- Get Last Known GPS Location

  - **GET** `/api/vehicles/{id}/last-location`
  - Path Variable:
    - `id` (Long): Vehicle ID
  - Response Body (Success):

  ```json
  {
    "message": "Last known location retrieved",
    "data": {
      "id": 5,
      "vehicle": {
        "id": 1,
        "plateNumber": "B1234XYZ",
        "name": "Truk 1",
        "type": "Truck"
      },
      "latitude": -6.2,
      "longitude": 106.8,
      "speed": 180,
      "timestamp": "2025-07-17T10:00:00",
      "speedViolation": true
    }
  }
  ```

  - Response Body(404 - not found)

  ```json
  {
    "message": "Vehicle not found"
  }
  ```

  ```json
  {
    "message": "No GPS log found"
  }
  ```

  - Response Body(500 - Server error)

  ```json
  {
    "message": "Unexpected error"
  }
  ```

- Get GPS Logs History by Vehicle ID

  - **GET** `/api/vehicles/{id}/gps-logs`
  - Path Variable:
    - `id` (Long): Vehicle ID
  - Query Parameters:
    - `startTime` Start timestamp for filtering logs
    - `endTime` End timestamp for filtering logs
  - Response Body (Success):

  ```json
  {
    "message": "GPS logs retrieved",
    "data": [
      {
        "id": 10,
        "vehicle": {
          "id": 1,
          "plateNumber": "B1234XYZ",
          "name": "Truk 1",
          "type": "Truck"
        },
        "latitude": -6.2,
        "longitude": 106.8,
        "speed": 60,
        "timestamp": "2025-07-17T08:00:00"
      },
      {
        "id": 11,
        "vehicle": {
          "id": 1,
          "plateNumber": "B1234XYZ",
          "name": "Truk 1",
          "type": "Truck"
        },
        "latitude": -6.25,
        "longitude": 106.75,
        "speed": 70,
        "timestamp": "2025-07-17T08:15:00"
      }
    ]
  }
  ```

  - Response Body (404 - Vehicle not found):

  ```json
  {
    "message": "Vehicle not found"
  }
  ```

  - Response Body (500 - Server error):

  ```json
  {
    "message": "Unexpected error"
  }
  ```

## Unit Testing
Unit tests are included to ensure that the core GPS-related functionalities work as expected.

- Test File Location:
```
src/test/java/com/GPS_Microservice/GPS/Microservice/GPSControllerTest.java
```
- Test framework: Junit 5, Mockito
- Coverage:
    - Save GPS log
    - Get last location
    - Get GPS history
    - Vehicle not found handling
- How to run tests:
```bash
./mvnw test
```
## Note
 If you are using Flyway and already have the schema created, make sure to disable migration during test runs to avoid conflict in application.properties:
 ```bash
 spring.flyway.enabled=false
 ```
