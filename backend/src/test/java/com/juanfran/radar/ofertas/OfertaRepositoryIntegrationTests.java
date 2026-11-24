package com.juanfran.radar.ofertas;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.juanfran.radar.TestcontainersConfiguration;

/**
 * Tests de {@link OfertaRepository} contra PostgreSQL real (Testcontainers), con el
 * esquema creado por Flyway (spec 001, CA-1, CA-4 y CA-11).
 *
 * {@code @DataJpaTest} solo arranca la capa de persistencia (JPA, DataSource y Flyway)
 * y ejecuta cada test en una transacción que se deshace al terminar, así que cada
 * test empieza con la tabla vacía. Como el contenedor llega por {@code @ServiceConnection},
 * no se sustituye por una base de datos embebida.
 */
@Import(TestcontainersConfiguration.class)
@DataJpaTest
class OfertaRepositoryIntegrationTests {

	private static final Instant FECHA = Instant.parse("2026-11-10T09:00:00Z");

	@Autowired
	private OfertaRepository repositorio;

	@Test
	void insertarLaMismaOfertaDosVecesDejaUnaSolaFila() {
		int primera = repositorio.insertarSiNoExiste(oferta("jobicy", "123"));
		int segunda = repositorio.insertarSiNoExiste(oferta("jobicy", "123"));

		assertThat(primera).isEqualTo(1);
		assertThat(segunda).isZero();
		assertThat(repositorio.count()).isEqualTo(1);
	}

	@Test
	void elMismoIdExternoEnFuentesDistintasSonOfertasDistintas() {
		repositorio.insertarSiNoExiste(oferta("jobicy", "123"));
		repositorio.insertarSiNoExiste(oferta("otra-fuente", "123"));

		assertThat(repositorio.count()).isEqualTo(2);
	}

	@Test
	void guardaTodosLosCamposDelModeloNormalizado() {
		Oferta completa = new Oferta("jobicy", "456", "Desarrollador Java Junior", "Acme", "Europa", FECHA,
				"https://jobicy.com/jobs/456", "full-time", "junior", new BigDecimal("30000"),
				new BigDecimal("40000.50"), "EUR", "<p>Descripción</p>");

		repositorio.insertarSiNoExiste(completa);

		Oferta guardada = repositorio.findAll().getFirst();
		assertThat(guardada.getId()).isNotNull();
		assertThat(guardada.getFuente()).isEqualTo("jobicy");
		assertThat(guardada.getIdExterno()).isEqualTo("456");
		assertThat(guardada.getTitulo()).isEqualTo("Desarrollador Java Junior");
		assertThat(guardada.getEmpresa()).isEqualTo("Acme");
		assertThat(guardada.getUbicacion()).isEqualTo("Europa");
		assertThat(guardada.getFechaPublicacion()).isEqualTo(FECHA);
		assertThat(guardada.getUrl()).isEqualTo("https://jobicy.com/jobs/456");
		assertThat(guardada.getTipoJornada()).isEqualTo("full-time");
		assertThat(guardada.getNivel()).isEqualTo("junior");
		assertThat(guardada.getSalarioMin()).isEqualByComparingTo("30000");
		assertThat(guardada.getSalarioMax()).isEqualByComparingTo("40000.50");
		assertThat(guardada.getMoneda()).isEqualTo("EUR");
		assertThat(guardada.getDescripcion()).isEqualTo("<p>Descripción</p>");
		assertThat(guardada.getCreadaEn()).isNotNull();
	}

	/** Oferta con solo los campos obligatorios; los opcionales van a {@code null}. */
	private static Oferta oferta(String fuente, String idExterno) {
		return new Oferta(fuente, idExterno, "Desarrollador Java", null, null, FECHA,
				"https://example.com/" + idExterno, null, null, null, null, null, null);
	}

}
