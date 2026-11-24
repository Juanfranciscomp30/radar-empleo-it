package com.juanfran.radar.conectores;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Modelo común de oferta que devuelven todos los conectores (spec 001, CA-1; plan §2.2).
 *
 * La fuente no va aquí: la pone {@code IngestaService} a partir de {@link Conector#fuente()},
 * para que un conector no pueda guardar ofertas a nombre de otra fuente.
 *
 * @param idExterno        identificador de la oferta en la fuente (obligatorio)
 * @param titulo           título (obligatorio)
 * @param empresa          empresa, si la fuente la da
 * @param ubicacion        región o "Cualquier ubicación", si la fuente la da
 * @param fechaPublicacion fecha de publicación (obligatoria)
 * @param url              URL de la oferta original (obligatoria)
 * @param tipoJornada      tipo de jornada, si la fuente lo da
 * @param nivel            nivel de experiencia, si la fuente lo da
 * @param salarioMin       salario mínimo, si la fuente lo da
 * @param salarioMax       salario máximo, si la fuente lo da
 * @param moneda           moneda del salario, si la fuente la da
 * @param descripcion      descripción, si la fuente la da (puede contener HTML; no se muestra)
 */
public record OfertaNormalizada(
		String idExterno,
		String titulo,
		String empresa,
		String ubicacion,
		Instant fechaPublicacion,
		String url,
		String tipoJornada,
		String nivel,
		BigDecimal salarioMin,
		BigDecimal salarioMax,
		String moneda,
		String descripcion) {
}
