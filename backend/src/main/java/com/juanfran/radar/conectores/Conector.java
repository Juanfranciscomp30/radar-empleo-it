package com.juanfran.radar.conectores;

import java.util.List;

/**
 * Fuente de ofertas (constitución III.1; plan §2.2).
 *
 * Cada fuente (Jobicy y, en la fase 2, otras) implementa esta interfaz y traduce sus
 * datos a {@link OfertaNormalizada}. El resto de la aplicación no sabe de qué fuente
 * viene una oferta. Cada implementación se registra como bean solo si está habilitada
 * por configuración (constitución III.2).
 */
public interface Conector {

	/** Nombre corto y estable de la fuente (p. ej. {@code jobicy}); se guarda en {@code ofertas.fuente}. */
	String fuente();

	/**
	 * Descarga las ofertas actuales de la fuente.
	 *
	 * Puede lanzar cualquier excepción si la fuente falla: {@code IngestaService} la
	 * captura y sigue con los demás conectores (constitución III.3).
	 */
	List<OfertaNormalizada> descargar();

}
