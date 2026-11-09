import { Injectable } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { CenterService } from './center.service';
import { DashboardStore } from '../../shared/state/dashboard-store';
import { GROUPES_SANGUINS } from '../../shared/constants/blood-groups';
import { isPast } from '../../shared/utils/date.utils';
import { rdvActif, rdvLabel } from '../../shared/utils/status.utils';
import { AppelDon, CentreDeDon, Creneau, DemandeDeSang, Donneur, RendezVous, Stock } from '../../shared/models/backend.model';

export type NiveauStock = 'Critique' | 'Faible' | 'Normal';

/** Données de l'espace Centre de don, partagées par toutes ses pages. */
@Injectable()
export class CenterStore extends DashboardStore {
  centre: CentreDeDon | null = null;
  demandes: DemandeDeSang[] = [];
  rendezVous: RendezVous[] = [];
  stocks: Stock[] = [];
  donneurs: Donneur[] = [];
  creneaux: Creneau[] = [];
  appels: AppelDon[] = [];

  constructor(private service: CenterService) { super(); }

  get ready() { return !!this.centre; }
  get displayName() { return this.centre?.nom ?? ''; }

  load() {
    this.loading = true;
    this.error = '';
    this.service.me().subscribe({
      next: me => {
        this.centre = me;
        forkJoin({
          demandes: this.service.demandes(me.id),
          rendezVous: this.service.rendezVous(me.id),
          stocks: this.service.stocks(me.id).pipe(catchError(() => of([] as Stock[]))),
          donneurs: this.service.donneurs().pipe(catchError(() => of([] as Donneur[]))),
          creneaux: this.service.creneaux(me.id).pipe(catchError(() => of([] as Creneau[]))),
          appels: this.service.appels(me.id).pipe(catchError(() => of([] as AppelDon[])))
        }).subscribe({
          next: r => {
            this.demandes = r.demandes;
            this.rendezVous = this.sortRdv(r.rendezVous);
            this.stocks = r.stocks;
            this.donneurs = [...r.donneurs].sort((a, b) => a.nom.localeCompare(b.nom));
            this.creneaux = r.creneaux;
            this.appels = r.appels;
            this.loading = false;
          },
          error: () => this.fail('Impossible de charger les demandes et rendez-vous.')
        });
      },
      error: () => this.fail('Impossible de charger votre profil (le backend est-il démarré ?).')
    });
  }

  // ---------- Stock ----------

  /** Les 8 groupes, même ceux sans ligne de stock (0 poche). */
  get stockLignes(): { groupe: string; quantite: number; niveau: NiveauStock }[] {
    return GROUPES_SANGUINS.map(g => {
      const quantite = this.stockDispo(g);
      return { groupe: g, quantite, niveau: this.niveau(quantite) };
    });
  }

  get stockTotal() { return this.stocks.reduce((t, s) => t + s.quantiteDisponible, 0); }

  get stocksCritiques() { return this.stockLignes.filter(s => s.niveau === 'Critique').length; }

  stockDispo(groupe: string): number {
    return this.stocks.find(s => s.groupeSanguin === groupe)?.quantiteDisponible ?? 0;
  }

  niveau(q: number): NiveauStock {
    if (q <= 5) return 'Critique';
    if (q <= 15) return 'Faible';
    return 'Normal';
  }

  definirStock(groupe: string, quantite: number) {
    if (!this.centre) return;
    this.run(this.service.definirStock(this.centre.id, groupe, quantite), saved => {
      this.stocks = [...this.stocks.filter(s => s.groupeSanguin !== groupe), saved];
      this.message = `Stock ${groupe} mis à jour : ${saved.quantiteDisponible} poche(s).`;
    });
  }

  // ---------- Demandes des hôpitaux ----------

  get enAttente() { return this.demandes.filter(d => d.statut === 'EN_ATTENTE').length; }

  repondre(d: DemandeDeSang, statut: 'ACCEPTEE' | 'REFUSEE', unites: number | undefined, done?: () => void) {
    if (!this.centre) return;
    this.run(this.service.repondre(this.centre.id, d.id, statut, unites), updated => {
      this.demandes = this.demandes.map(x => x.id === updated.id ? updated : x);
      this.refreshStocks();
      this.message = statut === 'ACCEPTEE'
        ? `Demande #${d.id} acceptée : ${updated.unitesFournies} poche(s) retirée(s) du stock.`
        : `Demande #${d.id} refusée.`;
      done?.();
    });
  }

  // ---------- Créneaux ----------

  get creneauxLibres() { return this.creneaux.filter(c => !isPast(c.date) && c.placesRestantes > 0).length; }

  ajouterCreneau(date: string, capacite: number, done?: () => void) {
    if (!this.centre) return;
    this.run(this.service.ajouterCreneau(this.centre.id, date, capacite), created => {
      this.creneaux = [...this.creneaux, created].sort((a, b) => a.date.localeCompare(b.date));
      this.message = 'Créneau ajouté.';
      done?.();
    });
  }

  supprimerCreneau(c: Creneau) {
    if (!this.centre) return;
    this.run(this.service.supprimerCreneau(this.centre.id, c.id), () => {
      this.creneaux = this.creneaux.filter(x => x.id !== c.id);
      this.message = 'Créneau supprimé.';
    });
  }

  // ---------- Appels aux donneurs ----------

  /** donneur renseigné = appel individuel, sinon appel à tous les disponibles du groupe. */
  lancerAppel(groupeSanguin: string, message: string, donneur: Donneur | null, done?: () => void) {
    if (!this.centre) return;
    const centreId = this.centre.id;
    const body = { groupeSanguin: groupeSanguin || undefined, message: message || undefined, donneurIds: donneur ? [donneur.id] : undefined };
    this.run(this.service.lancerAppel(centreId, body), r => {
      this.message = `Appel envoyé à ${r.donneursAppeles} donneur(s) : ils choisiront un de vos créneaux.`;
      this.service.appels(centreId).subscribe(a => this.appels = a);
      done?.();
    });
  }

  // ---------- Rendez-vous ----------

  get rdvAVenir() { return this.rendezVous.filter(r => rdvActif(r.statut)).length; }
  get rdvAConfirmer() { return this.rendezVous.filter(r => r.statut === 'PLANIFIE').length; }

  organiserDon(donneurEmail: string, date: string, done?: () => void) {
    if (!this.centre) return;
    this.run(this.service.organiserDon(this.centre.id, donneurEmail, date), created => {
      this.rendezVous = this.sortRdv([created, ...this.rendezVous]);
      this.message = 'Rendez-vous planifié, le donneur a été notifié.';
      done?.();
    });
  }

  changerStatut(r: RendezVous, statut: 'CONFIRME' | 'ANNULE' | 'ABSENT') {
    if (!this.centre) return;
    this.run(this.service.changerStatutRendezVous(this.centre.id, r.id, statut), updated => {
      this.replaceRdv(updated);
      this.refreshCreneaux();
      this.message = `Rendez-vous ${rdvLabel(statut).toLowerCase()}.`;
    });
  }

  // ---------- Dons ----------

  get dons() { return this.rendezVous.filter(r => r.don); }

  enregistrerDon(rdv: RendezVous, don: { dateDon: string; quantite: number; groupeSanguin?: string }, done?: () => void) {
    if (!this.centre) return;
    this.run(this.service.enregistrerDon(this.centre.id, rdv.id, don), updated => {
      this.replaceRdv(updated);
      if (updated.donneur) this.donneurs = this.donneurs.map(x => x.id === updated.donneur!.id ? updated.donneur! : x);
      this.refreshStocks();
      this.message = `Don enregistré : +1 poche ${updated.donneur?.groupeSanguin ?? ''} dans le stock.`;
      done?.();
    });
  }

  // ------------------------------------------------------------------

  private refreshCreneaux() {
    if (!this.centre) return;
    this.service.creneaux(this.centre.id).pipe(catchError(() => of(this.creneaux))).subscribe(c => this.creneaux = c);
  }

  private refreshStocks() {
    if (!this.centre) return;
    this.service.stocks(this.centre.id).pipe(catchError(() => of(this.stocks))).subscribe(s => this.stocks = s);
  }

  private replaceRdv(updated: RendezVous) {
    this.rendezVous = this.sortRdv(this.rendezVous.map(x => x.id === updated.id ? updated : x));
  }

  /** Prochains rendez-vous d'abord, terminés / annulés à la fin. */
  private sortRdv(list: RendezVous[]): RendezVous[] {
    const rang = (r: RendezVous) => rdvActif(r.statut) ? 0 : 1;
    return [...list].sort((a, b) => rang(a) - rang(b) || a.date.localeCompare(b.date));
  }
}
