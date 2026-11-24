/** Oferta del listado, tal y como la devuelve `GET /api/ofertas` (plan 001 §2.7). */
export interface Oferta {
  id: number;
  titulo: string;
  empresa: string | null;
  ubicacion: string | null;
  /** ISO-8601 en UTC, p. ej. `2026-11-20T09:00:00Z`. */
  fechaPublicacion: string;
  url: string;
  fuente: string;
  tipoJornada: string | null;
  nivel: string | null;
}

/** Página de ofertas de `GET /api/ofertas` (plan 001 §2.7). */
export interface PaginaOfertas {
  contenido: Oferta[];
  pagina: number;
  tamano: number;
  totalElementos: number;
  totalPaginas: number;
}
