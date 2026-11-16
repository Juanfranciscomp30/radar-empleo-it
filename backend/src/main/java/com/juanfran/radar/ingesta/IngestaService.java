package com.juanfran.radar.ingesta;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.juanfran.radar.conectores.Conector;
import com.juanfran.radar.conectores.OfertaNormalizada;
import com.juanfran.radar.ofertas.Oferta;
import com.juanfran.radar.ofertas.OfertaRepository;

/**
 * Recoge las ofertas de todos los conectores habilitados y las guarda sin duplicados
 * (spec 001, CA-1, CA-4 y CA-5; plan §2.6; ADR-0012).
 *
 * Cada conector se ejecuta aislado: si falla, se registra el error en el log y en su
 * resumen, y la ingesta sigue con los demás (constitución III.3). Este método nunca
 * lanza excepciones por culpa de un conector.
 */
@Service
public class IngestaService {

	private static final Logger log = LoggerFactory.getLogger(IngestaService.class);

	private final List<Conector> conectores;

	private final OfertaRepository repositorio;

	/** Spring inyecta todos los beans {@link Conector} habilitados (lista vacía si no hay ninguno). */
	public IngestaService(List<Conector> conectores, OfertaRepository repositorio) {
		this.conectores = List.copyOf(conectores);
		this.repositorio = repositorio;
	}

	/** Ejecuta la ingesta de todos los conectores y devuelve un resumen por fuente. */
	public List<ResumenIngesta> ejecutar() {
		List<ResumenIngesta> resumenes = new ArrayList<>();
		for (Conector conector : conectores) {
			resumenes.add(ingerir(conector));
		}
		return resumenes;
	}

	private ResumenIngesta ingerir(Conector conector) {
		String fuente = conector.fuente();
		int nuevas = 0;
		int existentes = 0;
		try {
			for (OfertaNormalizada oferta : conector.descargar()) {
				if (repositorio.insertarSiNoExiste(aOferta(fuente, oferta)) == 1) {
					nuevas++;
				}
				else {
					existentes++;
				}
			}
		}
		catch (RuntimeException e) {
			log.error("Ingesta de {} fallida tras {} nuevas y {} existentes", fuente, nuevas, existentes, e);
			return new ResumenIngesta(fuente, nuevas, existentes, descripcion(e));
		}
		log.info("Ingesta de {}: {} nuevas, {} existentes", fuente, nuevas, existentes);
		return new ResumenIngesta(fuente, nuevas, existentes, null);
	}

	private static Oferta aOferta(String fuente, OfertaNormalizada o) {
		return new Oferta(fuente, o.idExterno(), o.titulo(), o.empresa(), o.ubicacion(), o.fechaPublicacion(),
				o.url(), o.tipoJornada(), o.nivel(), o.salarioMin(), o.salarioMax(), o.moneda(), o.descripcion());
	}

	private static String descripcion(RuntimeException e) {
		return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
	}

}
