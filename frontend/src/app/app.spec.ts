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

  function render(): HTMLElement {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
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
    await fixture.whenStable();
    return fixture.nativeElement as HTMLElement;
  }

  it('muestra el título', () => {
    const el = render();
    httpMock.expectOne('/api/health');
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
});
