package com.juanfran.radar.ofertas;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Oferta guardada en la tabla {@code ofertas} (spec 001, CA-1; plan §2.3 y §2.4).
 *
 * Sin relaciones con otras tablas (ADR-0010). El esquema lo crea Flyway y Hibernate
 * solo lo valida ({@code ddl-auto=validate}), así que estos mapeos deben coincidir
 * con {@code V1__crear_tabla_ofertas.sql}.
 */
@Entity
@Table(name = "ofertas")
public class Oferta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String fuente;

	@Column(name = "id_externo", nullable = false, length = 100)
	private String idExterno;

	@Column(nullable = false)
	private String titulo;

	private String empresa;

	private String ubicacion;

	@Column(name = "fecha_publicacion", nullable = false)
	private Instant fechaPublicacion;

	@Column(nullable = false)
	private String url;

	@Column(name = "tipo_jornada")
	private String tipoJornada;

	private String nivel;

	@Column(name = "salario_min")
	private BigDecimal salarioMin;

	@Column(name = "salario_max")
	private BigDecimal salarioMax;

	@Column(length = 10)
	private String moneda;

	private String descripcion;

	/** Lo rellena la base de datos ({@code default now()}); Hibernate nunca lo escribe. */
	@Column(name = "creada_en", nullable = false, insertable = false, updatable = false)
	private Instant creadaEn;

	/** Constructor exigido por JPA. */
	protected Oferta() {
	}

	public Oferta(String fuente, String idExterno, String titulo, String empresa, String ubicacion,
			Instant fechaPublicacion, String url, String tipoJornada, String nivel, BigDecimal salarioMin,
			BigDecimal salarioMax, String moneda, String descripcion) {
		this.fuente = fuente;
		this.idExterno = idExterno;
		this.titulo = titulo;
		this.empresa = empresa;
		this.ubicacion = ubicacion;
		this.fechaPublicacion = fechaPublicacion;
		this.url = url;
		this.tipoJornada = tipoJornada;
		this.nivel = nivel;
		this.salarioMin = salarioMin;
		this.salarioMax = salarioMax;
		this.moneda = moneda;
		this.descripcion = descripcion;
	}

	public Long getId() {
		return id;
	}

	public String getFuente() {
		return fuente;
	}

	public String getIdExterno() {
		return idExterno;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getEmpresa() {
		return empresa;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public Instant getFechaPublicacion() {
		return fechaPublicacion;
	}

	public String getUrl() {
		return url;
	}

	public String getTipoJornada() {
		return tipoJornada;
	}

	public String getNivel() {
		return nivel;
	}

	public BigDecimal getSalarioMin() {
		return salarioMin;
	}

	public BigDecimal getSalarioMax() {
		return salarioMax;
	}

	public String getMoneda() {
		return moneda;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Instant getCreadaEn() {
		return creadaEn;
	}

}
