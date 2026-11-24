package com.juanfran.radar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

// Sin ingesta al arrancar: los tests no llaman a Jobicy (spec 001, CA-11)
@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "radar.ingesta.al-arrancar=false")
class RadarBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
