import { Injectable } from '@angular/core';
import { Observable, catchError, forkJoin, of } from 'rxjs';
import { DonorService } from './donor.service';
import { DashboardStore } from '../../shared/state/dashboard-store';
import { rdvActif } from '../../shared/utils/status.utils';
import { AppelDon, CentreDeDon, Creneau, Donneur, Eligibilite, NotificationDto, RendezVous } from '../../shared/models/backend.model';

/** Données de l'espace Donneur, partagées par toutes ses pages. */
@Injectable()
export class DonorStore extends DashboardStore {
  donneur: Donneur | null = null;
  eligibilite: Eligibilite | null = null;
  centres: CentreDeDon[] = [];
  rendezVous: RendezVous[] = [];
  notifications: NotificationDto[] = [];
  appels: AppelDon[] = [];

  constructor(private service: DonorService) { super(); }

  get ready() { return !!this.donneur; }
  get displayName() { return this.donneur?.nom ?? ''; }

  load() {
    this.loading = true;
    this.error = '';
    this.service.me().subscribe({
      next: me => {
        this.donneur = me;
        forkJoin({
          eligibilite: this.service.eligibilite(me.id),
          rendezVous: this.service.rendezVous(me.id),
          centres: this.service.centres().pipe(catchError(() => of([] as CentreDeDon[]))),
          notifications: this.service.notifications().pipe(catchError(() => of([] as NotificationDto[]))),
          appels: this.service.appels(me.id).pipe(catchError(() => of([] as AppelDon[])))
        }).subscribe({
          next: r => {
            this.eligibilite = r.eligibilite;
            this.rendezVous = this.sortRdv(r.rendezVous);
            this.centres = r.centres;
            this.notifications = r.notifications;
            this.appels = r.appels;
            this.loading = false;
          },
          error: () => this.fail('Impossible de charger vos rendez-vous.')
        });
      },
      error: () => this.fail('Impossible de charger votre profil (le backend est-il démarré ?).')
    });
  }

  // ---------- Profil / disponibilité ----------

  get profilIncomplet() { return !!this.eligibilite && !this.eligibilite.eligible && !!this.eligibilite.raison?.startsWith('Profil incomplet'); }

  /** Copie modifiable du profil, pour le formulaire. */
  profilFrom(d: Donneur): Partial<Donneur> {
    return { nom: d.nom, dateNaissance: d.dateNaissance, sexe: d.sexe ?? 'M', groupeSanguin: d.groupeSanguin, telephone: d.telephone,
      ville: d.ville, poids: d.poids, hemoglobine: d.hemoglobine, disponibilite: d.disponibilite ?? true };
  }

  toggleDisponibilite() {
    if (!this.donneur) return;
    this.saveProfil({ ...this.profilFrom(this.donneur), disponibilite: !this.donneur.disponibilite },
      d => this.message = d.disponibilite ? 'Vous êtes maintenant disponible pour donner.' : 'Vous êtes maintenant indisponible.');
  }

  enregistrerProfil(profil: Partial<Donneur>) {
    this.saveProfil(profil, () => this.message = 'Profil mis à jour.');
  }

  // ---------- Appels du centre ----------

  /** Créneaux libres du centre qui a lancé l'appel. */
  creneauxAppel(appel: AppelDon): Observable<Creneau[]> {
    return this.service.creneauxAppel(this.donneur!.id, appel.id);
  }

  accepterAppel(appel: AppelDon, creneau: Creneau, done?: () => void) {
    if (!this.donneur) return;
    this.run(this.service.accepterAppel(this.donneur.id, appel.id, creneau.id), rdv => {
      this.rendezVous = this.sortRdv([rdv, ...this.rendezVous]);
      this.appels = this.appels.filter(x => x.id !== appel.id);
      this.message = 'Merci ! Votre rendez-vous est confirmé.';
      done?.();
    });
  }

  refuserAppel(appel: AppelDon, done?: () => void) {
    if (!this.donneur) return;
    this.run(this.service.refuserAppel(this.donneur.id, appel.id), () => {
      this.appels = this.appels.filter(x => x.id !== appel.id);
      this.message = 'Appel refusé.';
      done?.();
    });
  }

  // ---------- Rendez-vous ----------

  get prochain(): RendezVous | undefined { return this.rendezVous.find(r => rdvActif(r.statut)); }
  get peutPrendreRdv() { return !!this.eligibilite?.eligible && !this.prochain; }

  prendreRendezVous(centreId: number, date: string, done?: () => void) {
    if (!this.donneur) return;
    this.run(this.service.prendreRendezVous(this.donneur.id, centreId, date), created => {
      this.rendezVous = this.sortRdv([created, ...this.rendezVous]);
      this.message = 'Rendez-vous demandé : le centre doit maintenant le confirmer.';
      done?.();
    });
  }

  annuler(r: RendezVous) {
    if (!this.donneur) return;
    this.run(this.service.annulerRendezVous(this.donneur.id, r.id), updated => {
      this.rendezVous = this.sortRdv(this.rendezVous.map(x => x.id === updated.id ? updated : x));
      this.message = 'Rendez-vous annulé.';
    });
  }

  // ---------- Historique / notifications ----------

  get dons() { return this.rendezVous.filter(r => r.don).sort((a, b) => b.don!.dateDon.localeCompare(a.don!.dateDon)); }
  get nonLues() { return this.notifications.filter(n => !n.lu).length; }

  // ------------------------------------------------------------------

  private saveProfil(data: Partial<Donneur>, onSuccess: (d: Donneur) => void) {
    if (!this.donneur) return;
    this.run(this.service.updateProfil(this.donneur.id, data), d => {
      this.donneur = d;
      onSuccess(d);
      // l'éligibilité dépend du profil
      this.service.eligibilite(d.id).subscribe(e => this.eligibilite = e);
    });
  }

  /** Rendez-vous à venir d'abord, puis les plus récents. */
  private sortRdv(list: RendezVous[]): RendezVous[] {
    return [...list].sort((a, b) => Number(rdvActif(b.statut)) - Number(rdvActif(a.statut)) || b.date.localeCompare(a.date));
  }
}
