package com.juanfran.radar.conectores.jobicy;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Respuesta del listado de la API de Jobicy, con solo los campos que usa el conector
 * (plan §2.5; forma real comprobada en el fixture {@code remote-jobs-engineering.json}).
 *
 * @param jobs ofertas devueltas; {@code null} si la respuesta no las trae (respuesta inválida)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record JobicyRespuesta(List<Oferta> jobs) {

	/**
	 * Oferta tal como la da Jobicy. Todos los campos pueden faltar: la validación la hace
	 * el conector, que descarta las ofertas incompletas en lugar de fallar entera la ingesta.
	 *
	 * @param jobGeo  regiones separadas por comas (p. ej. {@code "Germany,  Spain,  UK"})
	 * @param jobType lista de tipos de jornada (p. ej. {@code ["Full-Time"]})
	 * @param pubDate fecha ISO-8601 con zona (p. ej. {@code 2026-11-18T12:43:21+00:00}); se lee
	 *                como texto para que una fecha mal formada descarte solo esa oferta
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	record Oferta(
			Long id,
			String url,
			String jobTitle,
			String companyName,
			String jobGeo,
			List<String> jobType,
			String jobLevel,
			String jobDescription,
			String pubDate,
			BigDecimal salaryMin,
			BigDecimal salaryMax,
			String salaryCurrency) {
	}

}
