package com.juanfran.radar.ofertas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lectura del listado de ofertas (spec 001, CA-6; plan §2.7).
 */
@Service
public class OfertaService {

	static final int TAMANO_MAXIMO = 50;

	/**
	 * De la más reciente a la más antigua. El {@code id} desempata ofertas con la misma fecha,
	 * para que el orden sea estable entre páginas.
	 */
	private static final Sort ORDEN = Sort.by(Sort.Order.desc("fechaPublicacion"), Sort.Order.desc("id"));

	private final OfertaRepository repositorio;

	public OfertaService(OfertaRepository repositorio) {
		this.repositorio = repositorio;
	}

	/**
	 * Devuelve una página del listado. Los valores fuera de rango se ajustan: la página mínima es 0
	 * y el tamaño va de 1 a {@value #TAMANO_MAXIMO}. La respuesta indica el tamaño aplicado.
	 */
	@Transactional(readOnly = true)
	public PaginaOfertas listar(int pagina, int tamano) {
		int paginaAplicada = Math.max(pagina, 0);
		int tamanoAplicado = Math.clamp(tamano, 1, TAMANO_MAXIMO);
		Page<Oferta> resultado = repositorio.findAll(PageRequest.of(paginaAplicada, tamanoAplicado, ORDEN));
		return new PaginaOfertas(resultado.map(OfertaResumen::de).getContent(), paginaAplicada, tamanoAplicado,
				resultado.getTotalElements(), resultado.getTotalPages());
	}

}
