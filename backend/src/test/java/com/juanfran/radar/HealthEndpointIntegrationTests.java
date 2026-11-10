package com.juanfran.radar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Test de integración del endpoint de salud (spec 000, CA-2 y CA-5).
 *
 * Arranca la aplicación completa en un puerto aleatorio, conectada a un PostgreSQL
 * real levantado con Testcontainers, y hace una petición HTTP real a /api/health.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class HealthEndpointIntegrationTests {

	@Autowired
	private RestTestClient client;

	@Test
	void healthRespondeUpConBaseDeDatosAccesible() {
		client.get()
			.uri("/api/health")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.status").isEqualTo("UP")
			.jsonPath("$.components.db.status").isEqualTo("UP");
	}

}
