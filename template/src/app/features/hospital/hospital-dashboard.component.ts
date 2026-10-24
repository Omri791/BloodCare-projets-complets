import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { catchError, forkJoin, of } from 'rxjs';
import { HospitalService } from './hospital.service';
import { DemandeDeSang, Hopital, NotificationDto } from '../../shared/models/backend.model';

@Component({standalone:true,imports:[FormsModule,DatePipe],templateUrl:'./hospital-dashboard.component.html'})
export class HospitalDashboardComponent implements OnInit {
  loading = true;
  error = '';
  saving = false;
  showForm = false;

  hopital: Hopital | null = null;
  demandes: DemandeDeSang[] = [];
  notifications: NotificationDto[] = [];

  request = { groupeSanguin: 'O+', quantite: 2, urgence: 'URGENT', motif: '' };

  constructor(private service: HospitalService) {}

  ngOnInit() { this.load(); }

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

  submit() {
    if (!this.hopital || this.saving) return;
    this.saving = true;
    this.error = '';
    this.service.envoyerDemande(this.hopital.id, { ...this.request }).subscribe({
      next: created => {
        this.demandes = [created, ...this.demandes];
        this.showForm = false;
        this.saving = false;
        this.request = { groupeSanguin: 'O+', quantite: 2, urgence: 'URGENT', motif: '' };
      },
      error: () => { this.saving = false; this.error = "La demande n'a pas pu être envoyée."; }
    });
  }

  get total() { return this.demandes.length; }
  get enAttente() { return this.demandes.filter(d => d.statut === 'EN_ATTENTE').length; }
  get traitees() { return this.demandes.filter(d => d.statut !== 'EN_ATTENTE').length; }
  get nonLues() { return this.notifications.filter(n => !n.lu).length; }

  statutLabel(s: string): string {
    switch (s) {
      case 'EN_ATTENTE': return 'EN ATTENTE';
      case 'ACCEPTEE': return 'ACCEPTÉE';
      case 'REFUSEE': return 'REFUSÉE';
      default: return s.replace(/_/g, ' ');
    }
  }

  statutClass(s: string): string {
    if (s === 'EN_ATTENTE') return 'badge-warning';
    if (s === 'REFUSEE') return 'badge-danger';
    return 'badge-success';
  }

  private fail(msg: string) { this.error = msg; this.loading = false; }
}
