package com.juanfran.radar.ofertas;

import java.time.Instant;

/**
 * Oferta tal y como la devuelve el listado de la API (plan 001 §2.7).
 *
 * No incluye la descripción: no se muestra en el listado y reduce el tamaño de la respuesta.
 */
public record OfertaResumen(Long id, String titulo, String empresa, String ubicacion, Instant fechaPublicacion,
		String url, String fuente, String tipoJornada, String nivel) {

	static OfertaResumen de(Oferta o) {
		return new OfertaResumen(o.getId(), o.getTitulo(), o.getEmpresa(), o.getUbicacion(), o.getFechaPublicacion(),
				o.getUrl(), o.getFuente(), o.getTipoJornada(), o.getNivel());
	}

}
