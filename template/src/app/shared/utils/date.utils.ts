/** Maintenant, au format attendu par <input type="datetime-local"> (heure locale). */
export function nowLocalInput(): string {
  return new Date(Date.now() - new Date().getTimezoneOffset() * 60000).toISOString().slice(0, 16);
}

/** Aujourd'hui au format "yyyy-MM-dd". */
export function today(): string {
  return new Date().toISOString().slice(0, 10);
}

export function isPast(date: string): boolean {
  return new Date(date).getTime() < Date.now();
}
