package com.juanfran.radar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// Activa @Scheduled para la ingesta programada (plan 001 §2.6, ADR-0012)
@EnableScheduling
public class RadarBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RadarBackendApplication.class, args);
	}

}
