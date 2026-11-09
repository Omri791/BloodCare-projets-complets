import { Injectable } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { HospitalService } from './hospital.service';
import { DashboardStore } from '../../shared/state/dashboard-store';
import { DemandeDeSang, Hopital, NotificationDto, NouvelleDemande } from '../../shared/models/backend.model';

/** Données de l'espace Hôpital, partagées par toutes ses pages. */
@Injectable()
export class HospitalStore extends DashboardStore {
  hopital: Hopital | null = null;
  demandes: DemandeDeSang[] = [];
  notifications: NotificationDto[] = [];

  constructor(private service: HospitalService) { super(); }

  get ready() { return !!this.hopital; }
  get displayName() { return this.hopital?.nom ?? ''; }

  load() {
    this.loading = true;
    this.error = '';
    this.service.me().subscribe({
      next: me => {
        this.hopital = me;
        forkJoin({
          demandes: this.service.demandes(me.id),
          notifications: this.service.notifications().pipe(catchError(() => of([] as NotificationDto[])))
        }).subscribe({
          next: r => {
            // les plus récentes d'abord
            this.demandes = [...r.demandes].sort((a, b) => b.id - a.id);
            this.notifications = r.notifications;
            this.loading = false;
          },
          error: () => this.fail('Impossible de charger les demandes.')
        });
      },
      error: () => this.fail('Impossible de charger votre profil (le backend est-il démarré ?).')
    });
  }

  envoyerDemande(demande: NouvelleDemande, done?: () => void) {
    if (!this.hopital) return;
    this.run(this.service.envoyerDemande(this.hopital.id, demande), created => {
      this.demandes = [created, ...this.demandes];
      this.message = `Demande #${created.id} envoyée aux centres de don.`;
      done?.();
    }, () => "La demande n'a pas pu être envoyée.");
  }

  get total() { return this.demandes.length; }
  get enAttente() { return this.demandes.filter(d => d.statut === 'EN_ATTENTE').length; }
  get traitees() { return this.demandes.filter(d => d.statut !== 'EN_ATTENTE').length; }
  get nonLues() { return this.notifications.filter(n => !n.lu).length; }
}
