import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { AppelDon, CentreDeDon, Creneau, Donneur, Eligibilite, NotificationDto, RendezVous } from '../../shared/models/backend.model';

@Injectable({providedIn:'root'})
export class DonorService {
  constructor(private api: ApiService) {}

  /** Fiche du donneur connecté (GET /api/me) -> contient l'id utilisé ensuite. */
  me(): Observable<Donneur> { return this.api.get<Donneur>('/me'); }

  updateProfil(donneurId: number, profil: Partial<Donneur>): Observable<Donneur> {
    return this.api.put<Donneur>(`/donneurs/${donneurId}/profil`, profil);
  }

  /** Donneur.verifierEligibilite() */
  eligibilite(donneurId: number): Observable<Eligibilite> {
    return this.api.get<Eligibilite>(`/donneurs/${donneurId}/eligibilite`);
  }

  centres(): Observable<CentreDeDon[]> { return this.api.get<CentreDeDon[]>('/centres'); }

  /** Rendez-vous du donneur ; ceux qui contiennent un don forment son historique. */
  rendezVous(donneurId: number): Observable<RendezVous[]> {
    return this.api.get<RendezVous[]>(`/donneurs/${donneurId}/rendez-vous`);
  }

  /** Donneur.prendreRendezVous() : date au format "2026-11-20T09:30". */
  prendreRendezVous(donneurId: number, centreId: number, date: string): Observable<RendezVous> {
    return this.api.post<RendezVous>(`/donneurs/${donneurId}/rendez-vous`, { date, centreDeDon: { id: centreId } });
  }

  annulerRendezVous(donneurId: number, rdvId: number): Observable<RendezVous> {
    return this.api.patch<RendezVous>(`/donneurs/${donneurId}/rendez-vous/${rdvId}/annuler`, {});
  }

  notifications(): Observable<NotificationDto[]> {
    return this.api.get<NotificationDto[]>('/me/notifications');
  }

  // ---------- Appels du centre ----------

  /** Recevoir appel du centre : appels en attente de réponse. */
  appels(donneurId: number): Observable<AppelDon[]> {
    return this.api.get<AppelDon[]>(`/donneurs/${donneurId}/appels`);
  }

  /** Créneaux libres proposés par le centre qui a lancé l'appel. */
  creneauxAppel(donneurId: number, appelId: number): Observable<Creneau[]> {
    return this.api.get<Creneau[]>(`/donneurs/${donneurId}/appels/${appelId}/creneaux`);
  }

  /** Accepter l'appel en choisissant un créneau : crée un rendez-vous confirmé. */
  accepterAppel(donneurId: number, appelId: number, creneauId: number): Observable<RendezVous> {
    return this.api.post<RendezVous>(`/donneurs/${donneurId}/appels/${appelId}/accepter`, { creneauId });
  }

  refuserAppel(donneurId: number, appelId: number): Observable<AppelDon> {
    return this.api.post<AppelDon>(`/donneurs/${donneurId}/appels/${appelId}/refuser`, {});
  }
}
