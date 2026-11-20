package com.juanfran.radar.conectores.jobicy;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Registra el conector de Jobicy solo si {@code radar.conectores.jobicy.habilitado=true}
 * (constitución III.2, spec 001 CA-10; plan §2.2 y §2.5).
 *
 * Aquí se fijan los timeouts del cliente HTTP (CA-5): así el conector recibe un
 * {@link RestClient} ya configurado y en los tests se le puede pasar uno simulado.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "radar.conectores.jobicy", name = "habilitado", havingValue = "true")
@EnableConfigurationProperties(JobicyPropiedades.class)
class JobicyConfiguracion {

	static final Duration TIMEOUT_CONEXION = Duration.ofSeconds(5);

	static final Duration TIMEOUT_LECTURA = Duration.ofSeconds(15);

	@Bean
	JobicyConector jobicyConector(JobicyPropiedades propiedades) {
		HttpClient httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT_CONEXION).build();
		JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
		factory.setReadTimeout(TIMEOUT_LECTURA);
		RestClient restClient = RestClient.builder()
			.requestFactory(factory)
			.defaultHeader(HttpHeaders.USER_AGENT, "radar-empleo-it")
			.build();
		return new JobicyConector(restClient, propiedades);
	}

}
