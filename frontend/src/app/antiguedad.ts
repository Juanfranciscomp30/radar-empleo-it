const formato = new Intl.RelativeTimeFormat('es', { numeric: 'always' });

const MINUTO = 60_000;
const HORA = 60 * MINUTO;
const DIA = 24 * HORA;
const MES = 30 * DIA;
const ANO = 365 * DIA;

/**
 * Antigüedad de una fecha en español, p. ej. "hace 2 días" (spec 001, CA-7; plan §3).
 *
 * Función pura: recibe la hora actual para poder probarla. Se usa la unidad más grande
 * que quepa entera; por debajo de un minuto (o si la fecha está en el futuro por un desfase
 * de reloj) devuelve "ahora mismo". `numeric: 'always'` da "hace 2 días" en vez de "anteayer".
 */
export function antiguedadRelativa(fecha: string | Date, ahora: Date = new Date()): string {
  const transcurrido = ahora.getTime() - new Date(fecha).getTime();

  if (transcurrido < MINUTO) {
    return 'ahora mismo';
  }
  if (transcurrido < HORA) {
    return formato.format(-Math.floor(transcurrido / MINUTO), 'minute');
  }
  if (transcurrido < DIA) {
    return formato.format(-Math.floor(transcurrido / HORA), 'hour');
  }
  if (transcurrido < MES) {
    return formato.format(-Math.floor(transcurrido / DIA), 'day');
  }
  if (transcurrido < ANO) {
    return formato.format(-Math.floor(transcurrido / MES), 'month');
  }
  return formato.format(-Math.floor(transcurrido / ANO), 'year');
}
