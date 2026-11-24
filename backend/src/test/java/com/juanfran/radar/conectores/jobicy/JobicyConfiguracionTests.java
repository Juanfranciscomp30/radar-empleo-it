package com.juanfran.radar.conectores.jobicy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.juanfran.radar.conectores.Conector;

/**
 * El conector de Jobicy se activa o desactiva por configuración, sin tocar código
 * (constitución III.2; spec 001, CA-10).
 *
 * {@link ApplicationContextRunner} arranca un contexto mínimo con solo
 * {@link JobicyConfiguracion}: sin base de datos ni servidor web.
 */
class JobicyConfiguracionTests {

	private final ApplicationContextRunner contexto = new ApplicationContextRunner()
		.withUserConfiguration(JobicyConfiguracion.class)
		.withPropertyValues("radar.conectores.jobicy.url=https://jobicy.com/api/v2/remote-jobs",
				"radar.conectores.jobicy.cantidad=100");

	@Test
	void habilitadoRegistraElConector() {
		contexto.withPropertyValues("radar.conectores.jobicy.habilitado=true")
			.run(ctx -> assertThat(ctx).hasSingleBean(Conector.class)
				.getBean(Conector.class)
				.extracting(Conector::fuente)
				.isEqualTo("jobicy"));
	}

	@Test
	void deshabilitadoNoRegistraElConector() {
		contexto.withPropertyValues("radar.conectores.jobicy.habilitado=false")
			.run(ctx -> assertThat(ctx).doesNotHaveBean(Conector.class));
	}

	@Test
	void sinLaPropiedadNoRegistraElConector() {
		contexto.run(ctx -> assertThat(ctx).doesNotHaveBean(Conector.class));
	}

}
