package com.juanfran.radar.ingesta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.juanfran.radar.conectores.Conector;
import com.juanfran.radar.conectores.OfertaNormalizada;
import com.juanfran.radar.ofertas.Oferta;
import com.juanfran.radar.ofertas.OfertaRepository;

/**
 * Tests unitarios de {@link IngestaService} con Mockito (spec 001, CA-1, CA-5 y CA-11).
 *
 * Sin Spring ni base de datos: los conectores y el repositorio son dobles de prueba.
 */
class IngestaServiceTests {

	private static final Instant FECHA = Instant.parse("2026-11-10T09:00:00Z");

	private final OfertaRepository repositorio = mock(OfertaRepository.class);

	@Test
	void unConectorQueFallaNoImpideGuardarLasOfertasDeOtro() {
		Conector roto = conector("rota");
		when(roto.descargar()).thenThrow(new IllegalStateException("Timeout al llamar a la fuente"));
		Conector sano = conector("sana");
		when(sano.descargar()).thenReturn(List.of(oferta("1"), oferta("2")));
		when(repositorio.insertarSiNoExiste(any())).thenReturn(1);

		List<ResumenIngesta> resumen = new IngestaService(List.of(roto, sano), repositorio).ejecutar();

		assertThat(resumen).containsExactly(
				new ResumenIngesta("rota", 0, 0, "Timeout al llamar a la fuente"),
				new ResumenIngesta("sana", 2, 0, null));
		verify(repositorio).insertarSiNoExiste(argThat(o -> o.getIdExterno().equals("1")));
		verify(repositorio).insertarSiNoExiste(argThat(o -> o.getIdExterno().equals("2")));
	}

	@Test
	void cuentaLasOfertasNuevasYLasQueYaExistian() {
		Conector conector = conector("jobicy");
		when(conector.descargar()).thenReturn(List.of(oferta("1"), oferta("2"), oferta("3")));
		when(repositorio.insertarSiNoExiste(any())).thenReturn(1, 0, 1);

		List<ResumenIngesta> resumen = new IngestaService(List.of(conector), repositorio).ejecutar();

		assertThat(resumen).containsExactly(new ResumenIngesta("jobicy", 2, 1, null));
		assertThat(resumen.getFirst().conError()).isFalse();
	}

	@Test
	void guardaLaOfertaConLaFuenteDelConectorYTodosSusCampos() {
		Conector conector = conector("jobicy");
		when(conector.descargar()).thenReturn(List.of(oferta("42")));
		when(repositorio.insertarSiNoExiste(any())).thenReturn(1);

		new IngestaService(List.of(conector), repositorio).ejecutar();

		ArgumentCaptor<Oferta> guardada = ArgumentCaptor.forClass(Oferta.class);
		verify(repositorio).insertarSiNoExiste(guardada.capture());
		Oferta o = guardada.getValue();
		assertThat(o.getFuente()).isEqualTo("jobicy");
		assertThat(o.getIdExterno()).isEqualTo("42");
		assertThat(o.getTitulo()).isEqualTo("Desarrollador Java 42");
		assertThat(o.getEmpresa()).isEqualTo("Acme");
		assertThat(o.getUbicacion()).isEqualTo("Europa");
		assertThat(o.getFechaPublicacion()).isEqualTo(FECHA);
		assertThat(o.getUrl()).isEqualTo("https://example.com/42");
		assertThat(o.getTipoJornada()).isEqualTo("full-time");
		assertThat(o.getNivel()).isEqualTo("junior");
		assertThat(o.getDescripcion()).isEqualTo("Descripción");
	}

	@Test
	void unErrorAlGuardarSeRegistraEnElResumenSinDetenerLaIngesta() {
		Conector conector = conector("jobicy");
		when(conector.descargar()).thenReturn(List.of(oferta("1"), oferta("2")));
		when(repositorio.insertarSiNoExiste(any())).thenReturn(1).thenThrow(new RuntimeException());

		List<ResumenIngesta> resumen = new IngestaService(List.of(conector), repositorio).ejecutar();

		assertThat(resumen).containsExactly(new ResumenIngesta("jobicy", 1, 0, "RuntimeException"));
		assertThat(resumen.getFirst().conError()).isTrue();
	}

	@Test
	void sinConectoresNoHaceNada() {
		List<ResumenIngesta> resumen = new IngestaService(List.of(), repositorio).ejecutar();

		assertThat(resumen).isEmpty();
		verify(repositorio, never()).insertarSiNoExiste(any());
	}

	private static Conector conector(String fuente) {
		Conector conector = mock(Conector.class);
		when(conector.fuente()).thenReturn(fuente);
		return conector;
	}

	private static OfertaNormalizada oferta(String idExterno) {
		return new OfertaNormalizada(idExterno, "Desarrollador Java " + idExterno, "Acme", "Europa", FECHA,
				"https://example.com/" + idExterno, "full-time", "junior", null, null, null, "Descripción");
	}

}
