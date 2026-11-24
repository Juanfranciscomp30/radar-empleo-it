import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Subject, catchError, map, of, switchMap, tap } from 'rxjs';
import { antiguedadRelativa } from './antiguedad';
import { PaginaOfertas } from './oferta';
import { OfertasService } from './ofertas.service';

type Estado = 'cargando' | 'listo' | 'error';

/**
 * Listado de ofertas en tarjetas con paginación Anterior/Siguiente (spec 001, CA-7 y CA-8; plan §3).
 *
 * Tres estados: cargando, listo (con tarjetas o vacío) y error. Si se pulsa otra página antes de
 * que llegue la respuesta anterior, `switchMap` cancela la petición vieja y gana la última.
 */
@Component({
  selector: 'app-listado-ofertas',
  templateUrl: './listado-ofertas.html',
  styleUrl: './listado-ofertas.css',
})
export class ListadoOfertas {
  private readonly ofertasService = inject(OfertasService);
  private readonly paginasPedidas = new Subject<number>();

  protected readonly estado = signal<Estado>('cargando');
  protected readonly resultado = signal<PaginaOfertas | null>(null);

  protected readonly hayAnterior = computed(() => (this.resultado()?.pagina ?? 0) > 0);
  protected readonly haySiguiente = computed(() => {
    const r = this.resultado();
    return r !== null && r.pagina + 1 < r.totalPaginas;
  });

  protected readonly antiguedad = antiguedadRelativa;

  constructor() {
    this.paginasPedidas
      .pipe(
        tap(() => this.estado.set('cargando')),
        switchMap((pagina) =>
          this.ofertasService.listar(pagina).pipe(
            map((r): PaginaOfertas | null => r),
            catchError(() => of(null)),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((r) => {
        if (r === null) {
          this.estado.set('error');
        } else {
          this.resultado.set(r);
          this.estado.set('listo');
        }
      });
    this.paginasPedidas.next(0);
  }

  protected anterior(): void {
    this.irA((this.resultado()?.pagina ?? 0) - 1);
  }

  protected siguiente(): void {
    this.irA((this.resultado()?.pagina ?? 0) + 1);
  }

  private irA(pagina: number): void {
    this.paginasPedidas.next(Math.max(pagina, 0));
  }
}
