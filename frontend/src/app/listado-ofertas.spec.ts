import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListadoOfertas } from './listado-ofertas';
import { Oferta, PaginaOfertas } from './oferta';

const DIA = 24 * 60 * 60 * 1000;

function oferta(id: number, cambios: Partial<Oferta> = {}): Oferta {
  return {
    id,
    titulo: `Oferta ${id}`,
    empresa: `Empresa ${id}`,
    ubicacion: 'Europe',
    fechaPublicacion: new Date(Date.now() - 2 * DIA).toISOString(),
    url: `https://jobicy.com/jobs/${id}`,
    fuente: 'jobicy',
    tipoJornada: null,
    nivel: null,
    ...cambios,
  };
}

function pagina(contenido: Oferta[], numero = 0, totalPaginas = 1): PaginaOfertas {
  return { contenido, pagina: numero, tamano: 20, totalElementos: contenido.length, totalPaginas };
}

describe('ListadoOfertas', () => {
  let httpMock: HttpTestingController;
  let fixture: ComponentFixture<ListadoOfertas>;
  let el: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListadoOfertas],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(ListadoOfertas);
    el = fixture.nativeElement as HTMLElement;
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  async function responder(numero: number, respuesta: PaginaOfertas | 'error'): Promise<void> {
    const req = httpMock.expectOne(`/api/ofertas?pagina=${numero}&tamano=20`);
    if (respuesta === 'error') {
      req.flush(null, { status: 502, statusText: 'Bad Gateway' });
    } else {
      req.flush(respuesta);
    }
    await fixture.whenStable();
  }

  function boton(texto: string): HTMLButtonElement {
    const encontrado = Array.from(el.querySelectorAll('button')).find(
      (b) => b.textContent?.trim() === texto,
    );
    if (!encontrado) throw new Error(`No hay botón "${texto}"`);
    return encontrado;
  }

  it('muestra "Cargando" mientras llega la respuesta', async () => {
    expect(el.textContent).toContain('Cargando ofertas');
    await responder(0, pagina([]));
  });

  it('muestra una tarjeta por oferta con título, empresa, ubicación y antigüedad', async () => {
    await responder(0, pagina([oferta(1), oferta(2, { empresa: null, ubicacion: 'Spain' })]));

    const tarjetas = el.querySelectorAll('.tarjeta');
    expect(tarjetas.length).toBe(2);
    const primera = tarjetas[0];
    expect(primera.querySelector('.tarjeta__titulo')?.textContent).toContain('Oferta 1');
    expect(primera.querySelector('.tarjeta__empresa')?.textContent).toContain('Empresa 1');
    expect(primera.querySelector('.tarjeta__ubicacion')?.textContent).toContain('Europe');
    expect(primera.querySelector('.tarjeta__antiguedad')?.textContent).toContain('hace 2 días');
    expect(tarjetas[1].querySelector('.tarjeta__empresa')?.textContent).toContain(
      'Empresa no indicada',
    );
  });

  it('"Ver oferta" abre la oferta original en otra pestaña de forma segura', async () => {
    await responder(0, pagina([oferta(7)]));

    const enlace = el.querySelector<HTMLAnchorElement>('.tarjeta a');
    expect(enlace?.textContent?.trim()).toBe('Ver oferta');
    expect(enlace?.getAttribute('href')).toBe('https://jobicy.com/jobs/7');
    expect(enlace?.getAttribute('target')).toBe('_blank');
    expect(enlace?.getAttribute('rel')).toBe('noopener noreferrer');
  });

  it('muestra el estado vacío si no hay ofertas', async () => {
    await responder(0, pagina([]));

    expect(el.querySelector('.vacio')?.textContent).toContain('Todavía no hay ofertas');
    expect(el.querySelector('.tarjeta')).toBeNull();
  });

  it('muestra un error comprensible si el backend no responde', async () => {
    await responder(0, 'error');

    expect(el.querySelector('[role="alert"]')?.textContent).toContain(
      'No se ha podido conectar con el servidor',
    );
  });

  it('pagina con Anterior y Siguiente', async () => {
    await responder(0, pagina([oferta(1)], 0, 2));
    expect(el.textContent).toContain('Página 1 de 2');
    expect(boton('Anterior').disabled).toBe(true);
    expect(boton('Siguiente').disabled).toBe(false);

    boton('Siguiente').click();
    await responder(1, pagina([oferta(2)], 1, 2));
    expect(el.textContent).toContain('Página 2 de 2');
    expect(el.querySelector('.tarjeta__titulo')?.textContent).toContain('Oferta 2');
    expect(boton('Anterior').disabled).toBe(false);
    expect(boton('Siguiente').disabled).toBe(true);

    boton('Anterior').click();
    await responder(0, pagina([oferta(1)], 0, 2));
    expect(el.textContent).toContain('Página 1 de 2');
  });
});
