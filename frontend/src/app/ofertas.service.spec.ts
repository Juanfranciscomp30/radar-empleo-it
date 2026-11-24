import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PaginaOfertas } from './oferta';
import { OfertasService } from './ofertas.service';

describe('OfertasService', () => {
  let service: OfertasService;
  let httpMock: HttpTestingController;

  const pagina: PaginaOfertas = {
    contenido: [
      {
        id: 1,
        titulo: 'Desarrollador Java junior',
        empresa: 'Empresa',
        ubicacion: 'Europe',
        fechaPublicacion: '2026-11-20T09:00:00Z',
        url: 'https://jobicy.com/jobs/1',
        fuente: 'jobicy',
        tipoJornada: 'Full-Time',
        nivel: 'Junior',
      },
    ],
    pagina: 2,
    tamano: 10,
    totalElementos: 21,
    totalPaginas: 3,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(OfertasService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('pide la página indicada y devuelve la respuesta', () => {
    let resultado: PaginaOfertas | undefined;
    service.listar(2, 10).subscribe((p) => (resultado = p));

    const req = httpMock.expectOne('/api/ofertas?pagina=2&tamano=10');
    expect(req.request.method).toBe('GET');
    req.flush(pagina);

    expect(resultado).toEqual(pagina);
  });

  it('usa la página 0 y tamaño 20 por defecto', () => {
    service.listar().subscribe();

    httpMock.expectOne('/api/ofertas?pagina=0&tamano=20').flush(pagina);
  });

  it('propaga el error si el backend no responde', () => {
    let error: unknown;
    service.listar().subscribe({ error: (e) => (error = e) });

    httpMock
      .expectOne('/api/ofertas?pagina=0&tamano=20')
      .flush(null, { status: 502, statusText: 'Bad Gateway' });

    expect(error).toBeDefined();
  });
});
