import { antiguedadRelativa } from './antiguedad';

describe('antiguedadRelativa', () => {
  const ahora = new Date('2026-11-24T12:00:00Z');

  it('devuelve "ahora mismo" por debajo de un minuto', () => {
    expect(antiguedadRelativa('2026-11-24T11:59:30Z', ahora)).toBe('ahora mismo');
  });

  it('devuelve "ahora mismo" si la fecha está en el futuro', () => {
    expect(antiguedadRelativa('2026-11-24T12:05:00Z', ahora)).toBe('ahora mismo');
  });

  it('expresa los minutos', () => {
    expect(antiguedadRelativa('2026-11-24T11:59:00Z', ahora)).toBe('hace 1 minuto');
    expect(antiguedadRelativa('2026-11-24T11:15:00Z', ahora)).toBe('hace 45 minutos');
  });

  it('expresa las horas', () => {
    expect(antiguedadRelativa('2026-11-24T11:00:00Z', ahora)).toBe('hace 1 hora');
    expect(antiguedadRelativa('2026-11-24T02:30:00Z', ahora)).toBe('hace 9 horas');
  });

  it('expresa los días como "hace N días", no "ayer" ni "anteayer"', () => {
    expect(antiguedadRelativa('2026-11-23T12:00:00Z', ahora)).toBe('hace 1 día');
    expect(antiguedadRelativa('2026-11-22T10:00:00Z', ahora)).toBe('hace 2 días');
  });

  it('expresa los meses y los años', () => {
    expect(antiguedadRelativa('2026-09-20T12:00:00Z', ahora)).toBe('hace 2 meses');
    expect(antiguedadRelativa('2024-11-01T12:00:00Z', ahora)).toBe('hace 2 años');
  });

  it('acepta un objeto Date', () => {
    expect(antiguedadRelativa(new Date('2026-11-24T09:00:00Z'), ahora)).toBe('hace 3 horas');
  });
});
