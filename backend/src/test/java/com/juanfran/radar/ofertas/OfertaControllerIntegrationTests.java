package com.juanfran.radar.ofertas;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.juanfran.radar.TestcontainersConfiguration;

/**
 * {@code GET /api/ofertas} contra PostgreSQL real (Testcontainers): orden descendente por fecha
 * de publicación y paginación (spec 001, CA-6 y CA-11; plan §2.7).
 *
 * La ingesta al arrancar se desactiva para que los tests no llamen nunca a Jobicy.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "radar.ingesta.al-arrancar=false")
@AutoConfigureRestTestClient
class OfertaControllerIntegrationTests {

	@Autowired
	private RestTestClient client;

	@Autowired
	private OfertaRepository repositorio;

	@BeforeEach
	void prepararDatos() {
		repositorio.deleteAll();
		// Se insertan desordenadas a propósito: el orden debe venir de la consulta
		guardar("2", "Intermedia", "2026-11-10T09:00:00Z");
		guardar("1", "Antigua", "2026-11-01T09:00:00Z");
		guardar("3", "Reciente", "2026-11-20T09:00:00Z");
	}

	@Test
	void primeraPaginaDevuelveLasMasRecientesEnOrdenDescendente() {
		client.get()
			.uri("/api/ofertas?pagina=0&tamano=2")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.contenido.length()").isEqualTo(2)
			.jsonPath("$.contenido[0].titulo").isEqualTo("Reciente")
			.jsonPath("$.contenido[0].fechaPublicacion").isEqualTo("2026-11-20T09:00:00Z")
			.jsonPath("$.contenido[0].fuente").isEqualTo("jobicy")
			.jsonPath("$.contenido[0].url").isEqualTo("https://jobicy.com/jobs/3")
			.jsonPath("$.contenido[0].descripcion").doesNotExist()
			.jsonPath("$.contenido[1].titulo").isEqualTo("Intermedia")
			.jsonPath("$.pagina").isEqualTo(0)
			.jsonPath("$.tamano").isEqualTo(2)
			.jsonPath("$.totalElementos").isEqualTo(3)
			.jsonPath("$.totalPaginas").isEqualTo(2);
	}

	@Test
	void segundaPaginaDevuelveElResto() {
		client.get()
			.uri("/api/ofertas?pagina=1&tamano=2")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.contenido.length()").isEqualTo(1)
			.jsonPath("$.contenido[0].titulo").isEqualTo("Antigua")
			.jsonPath("$.pagina").isEqualTo(1);
	}

	@Test
	void sinParametrosUsaPaginaCeroYTamanoVeinte() {
		client.get()
			.uri("/api/ofertas")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.contenido.length()").isEqualTo(3)
			.jsonPath("$.pagina").isEqualTo(0)
			.jsonPath("$.tamano").isEqualTo(20);
	}

	@Test
	void elTamanoNoSuperaElMaximo() {
		client.get()
			.uri("/api/ofertas?tamano=500")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.tamano").isEqualTo(50);
	}

	private void guardar(String idExterno, String titulo, String fecha) {
		repositorio.insertarSiNoExiste(new Oferta("jobicy", idExterno, titulo, "Empresa", "Europe",
				Instant.parse(fecha), "https://jobicy.com/jobs/" + idExterno, null, null, null, null, null,
				"<p>Descripción</p>"));
	}

}
