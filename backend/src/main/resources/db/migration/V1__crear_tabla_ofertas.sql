-- Spec 001, plan §2.3: tabla de ofertas con el modelo normalizado común a todas las fuentes (CA-1).
-- Una migración fusionada en main nunca se modifica (ADR-0011): cualquier cambio es una V2, V3...

create table ofertas (
    id                bigserial    primary key,
    fuente            varchar(50)  not null,
    id_externo        varchar(100) not null,
    titulo            text         not null,
    empresa           text,
    ubicacion         text,
    fecha_publicacion timestamptz  not null,
    url               text         not null,
    tipo_jornada      text,
    nivel             text,
    salario_min       numeric,
    salario_max       numeric,
    moneda            varchar(10),
    descripcion       text,
    creada_en         timestamptz  not null default now(),

    -- Idempotencia (CA-4): una misma oferta de la misma fuente se guarda una sola vez
    constraint uk_ofertas_fuente_id_externo unique (fuente, id_externo)
);

-- Listado ordenado de la más reciente a la más antigua (CA-6)
create index idx_ofertas_fecha_publicacion on ofertas (fecha_publicacion desc);
