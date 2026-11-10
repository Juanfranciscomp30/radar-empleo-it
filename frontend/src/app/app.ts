import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { HealthService } from './health.service';

@Component({
  imports: [],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  /** `undefined` mientras llega la respuesta; después `true` o `false`. */
  protected readonly backendUp = toSignal(inject(HealthService).isBackendUp());
}
