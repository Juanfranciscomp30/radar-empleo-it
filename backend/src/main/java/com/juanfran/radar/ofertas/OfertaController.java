package com.juanfran.radar.ofertas;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code GET /api/ofertas?pagina=0&tamano=20}: ofertas guardadas, de la más reciente a la más
 * antigua y paginadas (spec 001, CA-6; plan §2.7).
 */
@RestController
public class OfertaController {

	private final OfertaService ofertaService;

	public OfertaController(OfertaService ofertaService) {
		this.ofertaService = ofertaService;
	}

	@GetMapping("/api/ofertas")
	public PaginaOfertas listar(@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamano) {
		return ofertaService.listar(pagina, tamano);
	}

}
