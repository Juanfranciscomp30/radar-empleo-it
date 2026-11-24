import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  /** El listado de ofertas pide su primera página al crearse; aquí basta con atenderla. */
  function atenderListado(): void {
    httpMock
      .expectOne((req) => req.url === '/api/ofertas')
      .flush({ contenido: [], pagina: 0, tamano: 20, totalElementos: 0, totalPaginas: 0 });
  }

  function render(): HTMLElement {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    httpMock.expectOne('/api/health');
    atenderListado();
    return fixture.nativeElement as HTMLElement;
  }

  async function renderWithResponse(ok: boolean): Promise<HTMLElement> {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const req = httpMock.expectOne('/api/health');
    if (ok) {
      req.flush({ status: 'UP' });
    } else {
      req.flush({ status: 'DOWN' }, { status: 503, statusText: 'Service Unavailable' });
    }
    atenderListado();
    await fixture.whenStable();
    return fixture.nativeElement as HTMLElement;
  }

  it('muestra el título', () => {
    const el = render();
    expect(el.querySelector('h1')?.textContent).toContain('Radar de Empleo IT');
  });

  it('muestra "Conectado" si el backend responde', async () => {
    const el = await renderWithResponse(true);
    expect(el.querySelector('.status')?.textContent?.trim()).toBe('Conectado');
  });

  it('muestra "Sin conexión" si el backend falla', async () => {
    const el = await renderWithResponse(false);
    expect(el.querySelector('.status')?.textContent?.trim()).toBe('Sin conexión');
  });

  it('incluye el listado de ofertas', () => {
    const el = render();
    expect(el.querySelector('app-listado-ofertas')).not.toBeNull();
  });

  it('muestra en el pie la atribución a Jobicy con enlace (CA-9)', () => {
    const el = render();
    const pie = el.querySelector('footer');
    expect(pie?.textContent).toContain('Ofertas remotas de Jobicy');
    const enlace = pie?.querySelector('a');
    expect(enlace?.getAttribute('href')).toBe('https://jobicy.com');
    expect(enlace?.getAttribute('rel')).toBe('noopener noreferrer');
  });
});
