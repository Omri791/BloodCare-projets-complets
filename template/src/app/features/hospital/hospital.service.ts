import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { DemandeDeSang, Hopital, NotificationDto, NouvelleDemande } from '../../shared/models/backend.model';

@Injectable({providedIn:'root'})
export class HospitalService {
  constructor(private api: ApiService) {}

  /** Fiche de l'hôpital connecté (GET /api/me) -> contient l'id utilisé ensuite. */
  me(): Observable<Hopital> { return this.api.get<Hopital>('/me'); }

  demandes(hopitalId: number): Observable<DemandeDeSang[]> {
    return this.api.get<DemandeDeSang[]>(`/hopitaux/${hopitalId}/demandes`);
  }

  envoyerDemande(hopitalId: number, demande: NouvelleDemande): Observable<DemandeDeSang> {
    return this.api.post<DemandeDeSang>(`/hopitaux/${hopitalId}/demandes`, demande);
  }

  notifications(): Observable<NotificationDto[]> {
    return this.api.get<NotificationDto[]>('/me/notifications');
  }
}
