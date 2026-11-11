package com.juanfran.radar.ofertas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositorio de ofertas (plan §2.4, ADR-0010).
 */
public interface OfertaRepository extends JpaRepository<Oferta, Long> {

	/**
	 * Guarda la oferta solo si no existe ya otra con la misma {@code (fuente, id_externo)}.
	 *
	 * La idempotencia (CA-4) la garantiza la restricción única de la base de datos,
	 * no Hibernate: así es segura incluso con ingestas simultáneas.
	 *
	 * @return {@code 1} si se ha insertado, {@code 0} si ya existía
	 */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = """
			insert into ofertas (fuente, id_externo, titulo, empresa, ubicacion, fecha_publicacion, url,
			                     tipo_jornada, nivel, salario_min, salario_max, moneda, descripcion)
			values (:#{#o.fuente}, :#{#o.idExterno}, :#{#o.titulo}, :#{#o.empresa}, :#{#o.ubicacion},
			        :#{#o.fechaPublicacion}, :#{#o.url}, :#{#o.tipoJornada}, :#{#o.nivel},
			        :#{#o.salarioMin}, :#{#o.salarioMax}, :#{#o.moneda}, :#{#o.descripcion})
			on conflict (fuente, id_externo) do nothing
			""")
	int insertarSiNoExiste(@Param("o") Oferta oferta);

}
