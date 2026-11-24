package com.juanfran.radar.conectores.jobicy;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.juanfran.radar.conectores.Conector;
import com.juanfran.radar.conectores.OfertaNormalizada;

/**
 * Conector de la API pública de Jobicy (spec 001, CA-1, CA-2 y CA-5; plan §2.5).
 *
 * Hace <b>una sola llamada</b> por ingesta, filtrada por la industria de desarrollo de
 * software, y filtra la región en local: solo se quedan las ofertas disponibles desde
 * España (CA-2). Las ofertas incompletas se descartan y se registran en el log.
 *
 * Si Jobicy falla (error HTTP, timeout o respuesta inválida), {@link #descargar()} lanza
 * la excepción y {@code IngestaService} la registra sin detener la aplicación (CA-5).
 */
public class JobicyConector implements Conector {

	private static final Logger log = LoggerFactory.getLogger(JobicyConector.class);

	static final String FUENTE = "jobicy";

	/** Slug de "Software Engineering" en la taxonomía de Jobicy (comprobado en T4). */
	static final String INDUSTRIA = "engineering";

	/** Regiones de {@code jobGeo} desde las que se puede trabajar en España (plan §2.5). */
	private static final Set<String> REGIONES_ACEPTADAS = Set.of("spain", "europe", "emea", "anywhere");

	private static final String CUALQUIER_UBICACION = "Cualquier ubicación";

	private final RestClient restClient;

	private final URI uri;

	JobicyConector(RestClient restClient, JobicyPropiedades propiedades) {
		this.restClient = restClient;
		this.uri = UriComponentsBuilder.fromUriString(propiedades.url())
			.queryParam("count", propiedades.cantidad())
			.queryParam("industry", INDUSTRIA)
			.build()
			.toUri();
	}

	@Override
	public String fuente() {
		return FUENTE;
	}

	@Override
	public List<OfertaNormalizada> descargar() {
		JobicyRespuesta respuesta = restClient.get().uri(uri).retrieve().body(JobicyRespuesta.class);
		if (respuesta == null || respuesta.jobs() == null) {
			throw new IllegalStateException("Respuesta de Jobicy inválida: no contiene el campo 'jobs'");
		}

		List<OfertaNormalizada> ofertas = new ArrayList<>();
		int fueraDeRegion = 0;
		int descartadas = 0;
		for (JobicyRespuesta.Oferta oferta : respuesta.jobs()) {
			if (oferta == null) {
				descartadas++;
			}
			else if (!enRegionAceptada(oferta.jobGeo())) {
				fueraDeRegion++;
			}
			else {
				OfertaNormalizada normalizada = normalizar(oferta);
				if (normalizada == null) {
					descartadas++;
				}
				else {
					ofertas.add(normalizada);
				}
			}
		}
		log.info("Jobicy: {} recibidas, {} fuera de región, {} descartadas por incompletas, {} válidas",
				respuesta.jobs().size(), fueraDeRegion, descartadas, ofertas.size());
		return ofertas;
	}

	/** {@code true} si alguna de las regiones de {@code jobGeo} (separadas por comas) es aceptada. */
	static boolean enRegionAceptada(String jobGeo) {
		return regiones(jobGeo).stream().anyMatch(r -> REGIONES_ACEPTADAS.contains(r.toLowerCase(Locale.ROOT)));
	}

	/** Traduce la oferta al modelo común, o devuelve {@code null} si le falta un campo obligatorio. */
	private static OfertaNormalizada normalizar(JobicyRespuesta.Oferta o) {
		Instant fecha = parsearFecha(o.pubDate());
		if (o.id() == null || estaVacio(o.url()) || estaVacio(o.jobTitle()) || fecha == null) {
			log.warn("Jobicy: oferta descartada por incompleta (id={}, url={}, título={}, fecha={})", o.id(),
					o.url(), o.jobTitle(), o.pubDate());
			return null;
		}
		return new OfertaNormalizada(
				String.valueOf(o.id()),
				o.jobTitle().strip(),
				vacioANulo(o.companyName()),
				ubicacion(o.jobGeo()),
				fecha,
				o.url().strip(),
				tipoJornada(o.jobType()),
				vacioANulo(o.jobLevel()),
				o.salaryMin(),
				o.salaryMax(),
				vacioANulo(o.salaryCurrency()),
				vacioANulo(o.jobDescription()));
	}

	/** {@code "Germany,  Spain"} → {@code "Germany, Spain"}; {@code "Anywhere"} → "Cualquier ubicación". */
	private static String ubicacion(String jobGeo) {
		return regiones(jobGeo).stream()
			.map(r -> r.equalsIgnoreCase("anywhere") ? CUALQUIER_UBICACION : r)
			.collect(Collectors.joining(", "));
	}

	private static List<String> regiones(String jobGeo) {
		if (jobGeo == null) {
			return List.of();
		}
		return Arrays.stream(jobGeo.split(",")).map(String::strip).filter(r -> !r.isEmpty()).toList();
	}

	private static String tipoJornada(List<String> jobType) {
		if (jobType == null) {
			return null;
		}
		return vacioANulo(jobType.stream()
			.filter(t -> !estaVacio(t))
			.map(String::strip)
			.collect(Collectors.joining(", ")));
	}

	private static Instant parsearFecha(String pubDate) {
		if (estaVacio(pubDate)) {
			return null;
		}
		try {
			return OffsetDateTime.parse(pubDate.strip()).toInstant();
		}
		catch (DateTimeParseException e) {
			return null;
		}
	}

	private static boolean estaVacio(String texto) {
		return texto == null || texto.isBlank();
	}

	private static String vacioANulo(String texto) {
		return estaVacio(texto) ? null : texto.strip();
	}

}
