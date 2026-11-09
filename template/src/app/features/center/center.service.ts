import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { AppelDon, CentreDeDon, Creneau, DemandeDeSang, Donneur, RendezVous, Stock } from '../../shared/models/backend.model';

@Injectable({providedIn:'root'})
export class CenterService {
  constructor(private api: ApiService) {}

  /** Fiche du centre connecté (GET /api/me) -> contient l'id utilisé ensuite. */
  me(): Observable<CentreDeDon> { return this.api.get<CentreDeDon>('/me'); }

  /** Demandes traitées par ce centre + nouvelles demandes en attente. */
  demandes(centreId: number): Observable<DemandeDeSang[]> {
    return this.api.get<DemandeDeSang[]>(`/centres/${centreId}/demandes`);
  }

  /** CentreDeDon.repondreDemande() : ACCEPTEE (retire les poches du stock) ou REFUSEE. */
  repondre(centreId: number, demandeId: number, statut: 'ACCEPTEE' | 'REFUSEE', unitesFournies?: number): Observable<DemandeDeSang> {
    return this.api.patch<DemandeDeSang>(`/centres/${centreId}/demandes/${demandeId}`, { statut, unitesFournies });
  }

  /** Lecture seule : sert à afficher le stock disponible avant d'accepter une demande. */
  stocks(centreId: number): Observable<Stock[]> {
    return this.api.get<Stock[]>(`/centres/${centreId}/stocks`);
  }

  /** Tous les donneurs (gestion des donneurs par le centre). */
  donneurs(): Observable<Donneur[]> { return this.api.get<Donneur[]>('/donneurs'); }

  /** CentreDeDon.gererStock() : fixe le nombre de poches d'un groupe sanguin. */
  definirStock(centreId: number, groupeSanguin: string, quantiteDisponible: number): Observable<Stock> {
    return this.api.put<Stock>(`/centres/${centreId}/stocks`, { groupeSanguin, quantiteDisponible });
  }

  rendezVous(centreId: number): Observable<RendezVous[]> {
    return this.api.get<RendezVous[]>(`/centres/${centreId}/rendez-vous`);
  }

  /** CentreDeDon.organiserDon() : date au format "2026-11-20T09:30". */
  organiserDon(centreId: number, donneurEmail: string, date: string): Observable<RendezVous> {
    return this.api.post<RendezVous>(`/centres/${centreId}/rendez-vous`, { donneurEmail, date });
  }

  changerStatutRendezVous(centreId: number, rdvId: number, statut: 'CONFIRME' | 'ANNULE' | 'ABSENT'): Observable<RendezVous> {
    return this.api.patch<RendezVous>(`/centres/${centreId}/rendez-vous/${rdvId}`, { statut });
  }

  /** RendezVous aboutit à Don : ajoute une poche au stock du centre. */
  enregistrerDon(centreId: number, rdvId: number, don: { dateDon: string; quantite: number; groupeSanguin?: string }): Observable<RendezVous> {
    return this.api.post<RendezVous>(`/centres/${centreId}/rendez-vous/${rdvId}/don`, don);
  }

  // ---------- Créneaux ----------

  creneaux(centreId: number): Observable<Creneau[]> {
    return this.api.get<Creneau[]>(`/centres/${centreId}/creneaux`);
  }

  ajouterCreneau(centreId: number, date: string, capacite: number): Observable<Creneau> {
    return this.api.post<Creneau>(`/centres/${centreId}/creneaux`, { date, capacite });
  }

  supprimerCreneau(centreId: number, creneauId: number): Observable<void> {
    return this.api.delete<void>(`/centres/${centreId}/creneaux/${creneauId}`);
  }

  // ---------- Appels aux donneurs ----------

  appels(centreId: number): Observable<AppelDon[]> {
    return this.api.get<AppelDon[]>(`/centres/${centreId}/appels`);
  }

  /** CentreDeDon.lancerAppelDonneurs() : donneurIds vide = tous les donneurs disponibles (du groupe choisi). */
  lancerAppel(centreId: number, appel: { groupeSanguin?: string; message?: string; donneurIds?: number[] }): Observable<{ donneursAppeles: number }> {
    return this.api.post<{ donneursAppeles: number }>(`/centres/${centreId}/appels`, appel);
  }
}
