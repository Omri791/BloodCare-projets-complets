import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, forkJoin, of } from 'rxjs';
import { AdminService } from './admin.service';
import { BackendRole, Utilisateur } from '../../shared/models/backend.model';

@Component({standalone:true,imports:[FormsModule],templateUrl:'./admin-dashboard.component.html'})
export class AdminDashboardComponent implements OnInit {
  loading = true;
  error = '';
  saving = false;
  showForm = false;
  filter: BackendRole | 'ALL' = 'ALL';

  users: Utilisateur[] = [];
  nbHopitaux = 0;
  nbCentres = 0;
  nbEnAttente = 0;

  newHopital = { nom: '', email: '', ville: '', adresse: '', type: 'Public', capaciteLits: 100 };

  readonly filters: { value: BackendRole | 'ALL'; label: string }[] = [
    { value: 'ALL', label: 'Tous' },
    { value: 'HOPITAL', label: 'Hôpitaux' },
    { value: 'CENTRE_DON', label: 'Centres' },
    { value: 'DONNEUR', label: 'Donneurs' },
    { value: 'ADMIN', label: 'Admins' }
  ];

  constructor(private service: AdminService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.error = '';
    forkJoin({
      users: this.service.utilisateurs(),
      hopitaux: this.service.hopitaux(),
      centres: this.service.centres().pipe(catchError(() => of([]))),
      attente: this.service.demandesEnAttente().pipe(catchError(() => of([])))
    }).subscribe({
      next: r => {
        this.users = r.users;
        this.nbHopitaux = r.hopitaux.length;
        this.nbCentres = r.centres.length;
        this.nbEnAttente = r.attente.length;
        this.loading = false;
      },
      error: () => { this.error = 'Impossible de charger les données (backend démarré ? rôle ADMIN ?).'; this.loading = false; }
    });
  }

  get filtered() { return this.filter === 'ALL' ? this.users : this.users.filter(u => u.role === this.filter); }

  roleLabel(r: BackendRole): string {
    switch (r) {
      case 'ADMIN': return 'Administrateur';
      case 'HOPITAL': return 'Hôpital';
      case 'CENTRE_DON': return 'Centre de don';
      case 'DONNEUR': return 'Donneur';
      default: return 'Utilisateur';
    }
  }

  creerHopital() {
    if (this.saving) return;
    this.saving = true;
    this.error = '';
    this.service.creerHopital({ ...this.newHopital }).subscribe({
      next: () => {
        this.saving = false;
        this.showForm = false;
        this.newHopital = { nom: '', email: '', ville: '', adresse: '', type: 'Public', capaciteLits: 100 };
        this.load();
      },
      error: e => {
        this.saving = false;
        this.error = e.status === 409 ? 'Cet email est déjà utilisé.' : "L'hôpital n'a pas pu être créé.";
      }
    });
  }

  supprimer(u: Utilisateur) {
    if (!confirm(`Supprimer ${u.nom} (${u.email}) ?`)) return;
    this.service.supprimerUtilisateur(u.id).subscribe({
      next: () => this.load(),
      error: () => this.error = "La suppression a échoué."
    });
  }
}
