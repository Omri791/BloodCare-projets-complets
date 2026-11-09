import { Injectable } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { AdminService } from './admin.service';
import { AuthService } from '../../core/services/auth.service';
import { DashboardStore } from '../../shared/state/dashboard-store';
import { CentreDeDon, DemandeDeSang, Hopital, Utilisateur } from '../../shared/models/backend.model';

/** Données de l'espace Administration, partagées par toutes ses pages. */
@Injectable()
export class AdminStore extends DashboardStore {
  users: Utilisateur[] = [];
  hopitaux: Hopital[] = [];
  centres: CentreDeDon[] = [];
  demandesEnAttente: DemandeDeSang[] = [];

  private loaded = false;

  constructor(private service: AdminService, private auth: AuthService) { super(); }

  get ready() { return this.loaded; }

  get displayName() {
    const u = this.auth.user();
    return u ? `${u.firstName} ${u.lastName}`.trim() || u.email : '';
  }

  load() {
    this.loading = true;
    this.error = '';
    forkJoin({
      users: this.service.utilisateurs(),
      hopitaux: this.service.hopitaux(),
      centres: this.service.centres().pipe(catchError(() => of([] as CentreDeDon[]))),
      attente: this.service.demandesEnAttente().pipe(catchError(() => of([] as DemandeDeSang[])))
    }).subscribe({
      next: r => {
        this.users = r.users;
        this.hopitaux = r.hopitaux;
        this.centres = r.centres;
        this.demandesEnAttente = r.attente;
        this.loaded = true;
        this.loading = false;
      },
      error: () => this.fail('Impossible de charger les données (backend démarré ? rôle ADMIN ?).')
    });
  }

  creerHopital(h: Partial<Hopital>, done?: () => void) {
    this.run(this.service.creerHopital(h), created => {
      this.message = `Hôpital « ${created.nom} » créé.`;
      this.load();
      done?.();
    }, e => e.status === 409 ? 'Cet email est déjà utilisé.' : "L'hôpital n'a pas pu être créé.");
  }

  supprimerUtilisateur(u: Utilisateur) {
    this.run(this.service.supprimerUtilisateur(u.id), () => {
      this.message = `${u.nom} a été supprimé.`;
      this.load();
    }, () => 'La suppression a échoué.');
  }
}
