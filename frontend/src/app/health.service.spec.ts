import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { HealthService } from './health.service';

describe('HealthService', () => {
  let service: HealthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(HealthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devuelve true cuando el backend responde correctamente', () => {
    let result: boolean | undefined;
    service.isBackendUp().subscribe((up) => (result = up));

    const req = httpMock.expectOne('/api/health');
    expect(req.request.method).toBe('GET');
    req.flush({ status: 'UP' });

    expect(result).toBe(true);
  });

  it('devuelve false cuando el backend responde con error', () => {
    let result: boolean | undefined;
    service.isBackendUp().subscribe((up) => (result = up));

    httpMock
      .expectOne('/api/health')
      .flush({ status: 'DOWN' }, { status: 503, statusText: 'Service Unavailable' });

    expect(result).toBe(false);
  });
});
