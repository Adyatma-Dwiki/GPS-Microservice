package com.GPS_Microservice.GPS.Microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GpsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GpsMicroserviceApplication.class, args);
	}

}
