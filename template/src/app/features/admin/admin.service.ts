import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { CentreDeDon, DemandeDeSang, Hopital, Utilisateur } from '../../shared/models/backend.model';

@Injectable({providedIn:'root'})
export class AdminService {
  constructor(private api: ApiService) {}

  utilisateurs(): Observable<Utilisateur[]> { return this.api.get<Utilisateur[]>('/utilisateurs'); }
  hopitaux(): Observable<Hopital[]> { return this.api.get<Hopital[]>('/hopitaux'); }
  centres(): Observable<CentreDeDon[]> { return this.api.get<CentreDeDon[]>('/centres'); }
  demandesEnAttente(): Observable<DemandeDeSang[]> { return this.api.get<DemandeDeSang[]>('/demandes?statut=EN_ATTENTE'); }

  creerHopital(h: Partial<Hopital>): Observable<Hopital> { return this.api.post<Hopital>('/hopitaux', h); }
  supprimerUtilisateur(id: number): Observable<void> { return this.api.delete<void>(`/utilisateurs/${id}`); }
}
