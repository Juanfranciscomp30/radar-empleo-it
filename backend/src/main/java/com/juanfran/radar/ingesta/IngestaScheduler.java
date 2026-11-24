package com.juanfran.radar.ingesta;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Lanza la ingesta automáticamente (spec 001, CA-3; plan §2.6; ADR-0012):
 * <ul>
 * <li>con la frecuencia de {@code radar.ingesta.cron} (por defecto cada 6 horas, uso justo de Jobicy);</li>
 * <li>una vez al arrancar, cuando la aplicación ya está lista, si {@code radar.ingesta.al-arrancar=true}.</li>
 * </ul>
 * {@link IngestaService} ya registra en el log el resumen de cada fuente y nunca falla por un conector.
 */
@Component
public class IngestaScheduler {

	private final IngestaService ingestaService;

	private final boolean alArrancar;

	public IngestaScheduler(IngestaService ingestaService,
			@Value("${radar.ingesta.al-arrancar:true}") boolean alArrancar) {
		this.ingestaService = ingestaService;
		this.alArrancar = alArrancar;
	}

	@Scheduled(cron = "${radar.ingesta.cron}")
	public void programada() {
		ingestaService.ejecutar();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void alArrancar() {
		if (alArrancar) {
			ingestaService.ejecutar();
		}
	}

}
