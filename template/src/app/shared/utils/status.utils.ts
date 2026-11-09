import { BackendRole } from '../models/backend.model';

/** Libellés et classes de badge des statuts renvoyés par le backend. */

// ---------- Demandes de sang ----------

export function demandeLabel(s: string): string {
  switch (s) {
    case 'EN_ATTENTE': return 'EN ATTENTE';
    case 'ACCEPTEE': return 'ACCEPTÉE';
    case 'REFUSEE': return 'REFUSÉE';
    default: return s.replace(/_/g, ' ');
  }
}

export function demandeClass(s: string): string {
  if (s === 'EN_ATTENTE') return 'badge-warning';
  if (s === 'REFUSEE') return 'badge-danger';
  return 'badge-success';
}

export function urgenceClass(u: string): string {
  if (u === 'CRITICAL') return 'badge-danger';
  if (u === 'URGENT') return 'badge-warning';
  return 'badge-neutral';
}

// ---------- Rendez-vous ----------

export function rdvLabel(s: string): string {
  switch (s) {
    case 'PLANIFIE': return 'PLANIFIÉ';
    case 'CONFIRME': return 'CONFIRMÉ';
    case 'ANNULE': return 'ANNULÉ';
    case 'TERMINE': return 'TERMINÉ';
    default: return s;
  }
}

export function rdvClass(s: string): string {
  if (s === 'PLANIFIE') return 'badge-warning';
  if (s === 'ANNULE' || s === 'ABSENT') return 'badge-danger';
  if (s === 'TERMINE') return 'badge-neutral';
  return 'badge-success';
}

/** Rendez-vous encore à venir (ni terminé, ni annulé). */
export function rdvActif(s: string): boolean {
  return s === 'PLANIFIE' || s === 'CONFIRME';
}

// ---------- Appels aux donneurs ----------

export function appelLabel(s: string): string {
  switch (s) {
    case 'EN_ATTENTE': return 'EN ATTENTE';
    case 'ACCEPTE': return 'ACCEPTÉ';
    case 'REFUSE': return 'REFUSÉ';
    default: return s;
  }
}

export function appelClass(s: string): string {
  if (s === 'EN_ATTENTE') return 'badge-warning';
  if (s === 'REFUSE') return 'badge-danger';
  return 'badge-success';
}

// ---------- Utilisateurs ----------

export function roleLabel(r: BackendRole): string {
  switch (r) {
    case 'ADMIN': return 'Administrateur';
    case 'HOPITAL': return 'Hôpital';
    case 'CENTRE_DON': return 'Centre de don';
    case 'DONNEUR': return 'Donneur';
    default: return 'Utilisateur';
  }
}
