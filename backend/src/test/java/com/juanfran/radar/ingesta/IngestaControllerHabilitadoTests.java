package com.juanfran.radar.ingesta;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Con {@code radar.ingesta.manual.habilitada=true}, {@code POST /api/ingestas} lanza la ingesta
 * y responde {@code 202} con el resumen por fuente (plan 001 §2.6). El servicio es un mock:
 * no hay base de datos ni llamadas a Jobicy.
 */
@WebMvcTest(controllers = IngestaController.class, properties = "radar.ingesta.manual.habilitada=true")
class IngestaControllerHabilitadoTests {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private IngestaService ingestaService;

	@Test
	void postIngestasDevuelve202ConElResumen() throws Exception {
		given(ingestaService.ejecutar()).willReturn(List.of(new ResumenIngesta("jobicy", 3, 6, null)));

		mvc.perform(post("/api/ingestas"))
			.andExpect(status().isAccepted())
			.andExpect(jsonPath("$[0].fuente").value("jobicy"))
			.andExpect(jsonPath("$[0].nuevas").value(3))
			.andExpect(jsonPath("$[0].existentes").value(6));
	}

}
