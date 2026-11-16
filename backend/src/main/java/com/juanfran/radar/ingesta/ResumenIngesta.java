package com.juanfran.radar.ingesta;

/**
 * Resultado de la ingesta de una fuente (plan §2.6).
 *
 * @param fuente     nombre de la fuente
 * @param nuevas     ofertas guardadas por primera vez
 * @param existentes ofertas que ya estaban guardadas (no se duplican, CA-4)
 * @param error      motivo del fallo del conector, o {@code null} si ha ido bien
 */
public record ResumenIngesta(String fuente, int nuevas, int existentes, String error) {

	public boolean conError() {
		return error != null;
	}

}
