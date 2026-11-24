package com.juanfran.radar.ingesta;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Disparo manual de la ingesta para pruebas (spec 001, CA-3; plan §2.6; ADR-0012).
 *
 * Solo existe si {@code radar.ingesta.manual.habilitada=true} (por defecto {@code false}):
 * si no, el bean no se crea y {@code POST /api/ingestas} responde {@code 404}. Así no se
 * expone una operación que consume la API de un tercero. Nunca se activa en una demo pública.
 */
@RestController
@ConditionalOnProperty(name = "radar.ingesta.manual.habilitada", havingValue = "true")
public class IngestaController {

	private final IngestaService ingestaService;

	public IngestaController(IngestaService ingestaService) {
		this.ingestaService = ingestaService;
	}

	@PostMapping("/api/ingestas")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public List<ResumenIngesta> ejecutar() {
		return ingestaService.ejecutar();
	}

}
