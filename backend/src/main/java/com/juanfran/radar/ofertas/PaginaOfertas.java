package com.juanfran.radar.ofertas;

import java.util.List;

/**
 * Respuesta paginada de {@code GET /api/ofertas} (plan 001 §2.7).
 *
 * DTO propio: no se serializa el {@code Page} de Spring, cuyo formato JSON no es un contrato estable.
 */
public record PaginaOfertas(List<OfertaResumen> contenido, int pagina, int tamano, long totalElementos,
		int totalPaginas) {

}
