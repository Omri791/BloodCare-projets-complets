/** Une entrée du menu latéral d'un espace. */
export interface SidebarItem {
  label: string;
  /** Chemin absolu, ex. '/dashboard/center/stock'. */
  path: string;
  /** Pastille (nombre d'éléments à traiter) ; rien n'est affiché si 0 ou vide. */
  badge?: () => number | null | undefined;
}
