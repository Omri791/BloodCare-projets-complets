/** Modèles qui reflètent les entités JSON renvoyées par Spring Boot. */

export type BackendRole = 'ADMIN' | 'HOPITAL' | 'CENTRE_DON' | 'DONNEUR' | 'UTILISATEUR';

export interface Utilisateur {
  id: number;
  nom: string;
  email: string;
  role: BackendRole;
}

export interface Hopital extends Utilisateur {
  adresse?: string;
  ville?: string;
  type?: string;
  capaciteLits?: number;
}

export interface CentreDeDon extends Utilisateur {
  adresse?: string;
  ville?: string;
  capaciteStock?: number;
  horaires?: string;
}

export interface DemandeDeSang {
  id: number;
  dateCreation: string;
  groupeSanguin: string;
  composant?: string;
  quantite: number;
  urgence: string;
  motif?: string;
  statut: string;
  unitesFournies?: number;
}

export interface NouvelleDemande {
  groupeSanguin: string;
  quantite: number;
  urgence: string;
  motif?: string;
  composant?: string;
}

export interface NotificationDto {
  id: number;
  message: string;
  date: string;
  type?: string;
  lu: boolean;
}
