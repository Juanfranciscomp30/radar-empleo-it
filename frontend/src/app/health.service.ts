import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';

/**
 * Consulta el endpoint de salud del backend.
 * La ruta es relativa: en desarrollo la resuelve el proxy de `ng serve`
 * y en Docker la resuelve nginx (ADR-0006).
 */
@Injectable({ providedIn: 'root' })
export class HealthService {
  private readonly http = inject(HttpClient);

  /** Emite `true` si el backend responde correctamente y `false` ante cualquier error. */
  isBackendUp(): Observable<boolean> {
    return this.http.get('/api/health').pipe(
      map(() => true),
      catchError(() => of(false)),
    );
  }
}
