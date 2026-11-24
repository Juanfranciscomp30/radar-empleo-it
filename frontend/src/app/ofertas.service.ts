import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PaginaOfertas } from './oferta';

/**
 * Consulta el listado paginado de ofertas del backend (spec 001, CA-7; plan §3).
 * La ruta es relativa: en desarrollo la resuelve el proxy de `ng serve`
 * y en Docker la resuelve nginx (ADR-0006).
 */
@Injectable({ providedIn: 'root' })
export class OfertasService {
  private readonly http = inject(HttpClient);

  /**
   * Pide una página de ofertas, de la más reciente a la más antigua.
   * Los errores se propagan: el componente decide cómo mostrarlos (CA-8).
   */
  listar(pagina = 0, tamano = 20): Observable<PaginaOfertas> {
    const params = new HttpParams().set('pagina', pagina).set('tamano', tamano);
    return this.http.get<PaginaOfertas>('/api/ofertas', { params });
  }
}
