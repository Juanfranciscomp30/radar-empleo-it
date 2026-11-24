package com.juanfran.radar.ingesta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.juanfran.radar.TestcontainersConfiguration;

/**
 * Con la configuración por defecto ({@code radar.ingesta.manual.habilitada=false}) el disparo
 * manual no existe: {@code POST /api/ingestas} responde {@code 404} (spec 001, CA-3; ADR-0012).
 *
 * Aplicación completa con PostgreSQL real (Testcontainers). La ingesta al arrancar se desactiva
 * para que los tests no llamen nunca a Jobicy (CA-11).
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "radar.ingesta.al-arrancar=false")
@AutoConfigureRestTestClient
class IngestaControllerDeshabilitadoIntegrationTests {

	@Autowired
	private RestTestClient client;

	@Test
	void postIngestasDevuelve404ConElFlagDesactivado() {
		client.post().uri("/api/ingestas").exchange().expectStatus().isNotFound();
	}

}
