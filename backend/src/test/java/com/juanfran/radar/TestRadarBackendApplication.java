package com.juanfran.radar;

import org.springframework.boot.SpringApplication;

public class TestRadarBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(RadarBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
