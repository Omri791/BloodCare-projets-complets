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
  delaiTraitement?: number;
  hopital?: Hopital;
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

export interface Stock {
  id: number;
  groupeSanguin: string;
  quantiteDisponible: number;
  derniereMaj?: string;
}

export interface Donneur extends Utilisateur {
  dateNaissance?: string;
  sexe?: string;
  groupeSanguin?: string;
  telephone?: string;
  ville?: string;
  poids?: number;
  hemoglobine?: number;
  disponibilite?: boolean;
  totalDons?: number;
  dernierDon?: string;
}

export interface Eligibilite {
  eligible: boolean;
  raison?: string | null;
}

export interface Don {
  id: number;
  dateDon: string;
  quantite: number;
}

export interface RendezVous {
  id: number;
  date: string;
  statut: string;
  donneur?: Donneur;
  centreDeDon?: CentreDeDon;
  don?: Don | null;
}

export interface Creneau {
  id: number;
  date: string;
  capacite: number;
  placesRestantes: number;
}

export interface AppelDon {
  id: number;
  dateCreation: string;
  groupeSanguin?: string | null;
  message: string;
  statut: string;
  centreDeDon?: CentreDeDon;
  donneur?: Donneur;
}
