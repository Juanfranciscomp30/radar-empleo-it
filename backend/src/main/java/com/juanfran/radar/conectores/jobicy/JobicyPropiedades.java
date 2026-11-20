package com.juanfran.radar.conectores.jobicy;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración del conector de Jobicy ({@code radar.conectores.jobicy.*}; plan §2.5).
 *
 * Los valores por defecto están en {@code application.yml}. {@code habilitado} no se lee
 * aquí: lo usa {@code @ConditionalOnProperty} en {@link JobicyConfiguracion} (CA-10).
 *
 * @param url      URL del listado de ofertas de la API de Jobicy
 * @param cantidad número de ofertas que se piden en cada ingesta (una sola llamada)
 */
@ConfigurationProperties("radar.conectores.jobicy")
public record JobicyPropiedades(String url, int cantidad) {
}
