package com.juanfran.radar.conectores.jobicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withTooManyRequests;

import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.juanfran.radar.conectores.OfertaNormalizada;

/**
 * Tests de {@link JobicyConector} con respuestas simuladas (spec 001, CA-1, CA-2, CA-5 y CA-11).
 *
 * {@link MockRestServiceServer} sustituye al servidor de Jobicy: <b>no se llama a internet</b>.
 * El caso principal usa la respuesta real capturada en T4 ({@code fixtures/jobicy/}), recortada a
 * 12 ofertas y con las descripciones abreviadas para no redistribuir el contenido de Jobicy.
 */
class JobicyConectorTests {

	private static final String URL = "https://jobicy.com/api/v2/remote-jobs";

	private static final String URL_ESPERADA = URL + "?count=100&industry=engineering";

	private static final Resource FIXTURE = new ClassPathResource("fixtures/jobicy/remote-jobs-engineering.json");

	private MockRestServiceServer servidor;

	private JobicyConector conector;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		servidor = MockRestServiceServer.bindTo(builder).build();
		conector = new JobicyConector(builder.build(), new JobicyPropiedades(URL, 100));
	}

	@Test
	void haceUnaSolaLlamadaFiltradaPorIndustria() {
		servidor.expect(once(), requestTo(URL_ESPERADA))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(FIXTURE, MediaType.APPLICATION_JSON));

		conector.descargar();

		servidor.verify();
	}

	@Test
	void conLaRespuestaRealSoloSeQuedaConLasOfertasDisponiblesDesdeEspana() {
		responderCon(FIXTURE);

		List<OfertaNormalizada> ofertas = conector.descargar();

		// De las 12 ofertas del fixture, solo estas tienen Spain, Europe, EMEA o Anywhere en jobGeo.
		// Se descartan USA, "Canada, USA", LATAM, Germany y una lista de países europeos sin Spain
		assertThat(ofertas).extracting(OfertaNormalizada::idExterno)
			.containsExactly("155959", "163233", "155928", "155936", "155893", "163116", "163106");
	}

	@Test
	void mapeaTodosLosCamposDeUnaOfertaReal() {
		responderCon(FIXTURE);

		OfertaNormalizada oferta = buscar(conector.descargar(), "163106");

		assertThat(oferta.titulo()).isEqualTo("Senior QA Engineer");
		assertThat(oferta.empresa()).isEqualTo("Cross River");
		assertThat(oferta.ubicacion()).isEqualTo("Spain");
		assertThat(oferta.fechaPublicacion()).isEqualTo(Instant.parse("2026-11-18T12:42:34Z"));
		assertThat(oferta.url()).isEqualTo("https://jobicy.com/jobs/163106-senior-qa-engineer-5");
		assertThat(oferta.tipoJornada()).isEqualTo("Full-Time");
		assertThat(oferta.nivel()).isEqualTo("Senior");
		assertThat(oferta.salarioMin()).isEqualByComparingTo("55000");
		assertThat(oferta.salarioMax()).isEqualByComparingTo("70000");
		assertThat(oferta.moneda()).isEqualTo("EUR");
		assertThat(oferta.descripcion()).startsWith("<p><strong>Who We Are</strong></p>");
	}

	@Test
	void normalizaLaUbicacionYDejaANuloElSalarioQueNoViene() {
		responderCon(FIXTURE);

		List<OfertaNormalizada> ofertas = conector.descargar();

		assertThat(buscar(ofertas, "155893").ubicacion()).isEqualTo("Germany, Ireland, Spain, UK");
		OfertaNormalizada cualquiera = buscar(ofertas, "155928");
		assertThat(cualquiera.ubicacion()).isEqualTo("Cualquier ubicación");
		OfertaNormalizada sinSalario = buscar(ofertas, "163116");
		assertThat(sinSalario.salarioMin()).isNull();
		assertThat(sinSalario.salarioMax()).isNull();
		assertThat(sinSalario.moneda()).isNull();
	}

	@Test
	void filtraLaRegionPorCadaValorYNoPorSubcadena() {
		assertThat(JobicyConector.enRegionAceptada("Spain")).isTrue();
		assertThat(JobicyConector.enRegionAceptada("EMEA")).isTrue();
		assertThat(JobicyConector.enRegionAceptada("europe")).isTrue();
		assertThat(JobicyConector.enRegionAceptada("Anywhere")).isTrue();
		assertThat(JobicyConector.enRegionAceptada("USA,  Spain")).isTrue();
		assertThat(JobicyConector.enRegionAceptada("USA,  Canada")).isFalse();
		assertThat(JobicyConector.enRegionAceptada("Central Europe Only")).isFalse();
		assertThat(JobicyConector.enRegionAceptada("")).isFalse();
		assertThat(JobicyConector.enRegionAceptada(null)).isFalse();
	}

	@Test
	void descartaLasOfertasIncompletasYConservaLasDemas() {
		responderCon("""
				{"jobs": [
				  %s,
				  {"url": "https://jobicy.com/jobs/2", "jobTitle": "Sin id", "jobGeo": "Spain",
				   "pubDate": "2026-11-18T12:00:00+00:00"},
				  {"id": 3, "jobTitle": "Sin url", "jobGeo": "Spain", "pubDate": "2026-11-18T12:00:00+00:00"},
				  {"id": 4, "url": "https://jobicy.com/jobs/4", "jobTitle": "  ", "jobGeo": "Spain",
				   "pubDate": "2026-11-18T12:00:00+00:00"},
				  {"id": 5, "url": "https://jobicy.com/jobs/5", "jobTitle": "Fecha rota", "jobGeo": "Spain",
				   "pubDate": "ayer"},
				  null
				]}
				""".formatted(ofertaMinima(1, "EMEA")));

		List<OfertaNormalizada> ofertas = conector.descargar();

		assertThat(ofertas).extracting(OfertaNormalizada::idExterno).containsExactly("1");
		OfertaNormalizada minima = ofertas.getFirst();
		assertThat(minima.ubicacion()).isEqualTo("EMEA");
		assertThat(minima.empresa()).isNull();
		assertThat(minima.tipoJornada()).isNull();
		assertThat(minima.nivel()).isNull();
		assertThat(minima.descripcion()).isNull();
	}

	@Test
	void unaRespuestaSinOfertasDevuelveUnaListaVacia() {
		responderCon("{\"jobs\": []}");

		assertThat(conector.descargar()).isEmpty();
	}

	@Test
	void unErrorHttpDelServidorSePropaga() {
		servidor.expect(requestTo(URL_ESPERADA)).andRespond(withServerError());

		assertThatThrownBy(conector::descargar).isInstanceOf(HttpServerErrorException.class);
	}

	@Test
	void unLimiteDeUsoSePropaga() {
		servidor.expect(requestTo(URL_ESPERADA)).andRespond(withTooManyRequests());

		assertThatThrownBy(conector::descargar).isInstanceOf(HttpClientErrorException.TooManyRequests.class);
	}

	@Test
	void unTimeoutSePropaga() {
		servidor.expect(requestTo(URL_ESPERADA)).andRespond(withException(new SocketTimeoutException("Read timed out")));

		assertThatThrownBy(conector::descargar).isInstanceOf(ResourceAccessException.class)
			.hasRootCauseInstanceOf(SocketTimeoutException.class);
	}

	@Test
	void unJsonInvalidoSePropaga() {
		responderCon("<html>Mantenimiento</html>");

		assertThatThrownBy(conector::descargar).isInstanceOf(RestClientException.class);
	}

	@Test
	void unaRespuestaSinCampoJobsEsInvalida() {
		responderCon("{\"success\": false, \"statusCode\": 500}");

		assertThatThrownBy(conector::descargar).isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("jobs");
	}

	private void responderCon(Resource cuerpo) {
		servidor.expect(requestTo(URL_ESPERADA)).andRespond(withSuccess(cuerpo, MediaType.APPLICATION_JSON));
	}

	private void responderCon(String cuerpo) {
		servidor.expect(requestTo(URL_ESPERADA)).andRespond(withSuccess(cuerpo, MediaType.APPLICATION_JSON));
	}

	private static OfertaNormalizada buscar(List<OfertaNormalizada> ofertas, String idExterno) {
		return ofertas.stream().filter(o -> o.idExterno().equals(idExterno)).findFirst().orElseThrow();
	}

	private static String ofertaMinima(int id, String jobGeo) {
		return """
				{"id": %d, "url": "https://jobicy.com/jobs/%d", "jobTitle": "Oferta %d", "jobGeo": "%s",
				 "pubDate": "2026-11-18T12:00:00+00:00"}""".formatted(id, id, id, jobGeo);
	}

}
