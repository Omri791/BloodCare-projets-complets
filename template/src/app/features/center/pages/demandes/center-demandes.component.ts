import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CenterStore } from '../../center.store';
import { DemandeDeSang } from '../../../../shared/models/backend.model';
import { demandeClass, demandeLabel, urgenceClass } from '../../../../shared/utils/status.utils';

type FiltreDemandes = 'EN_ATTENTE' | 'TRAITEES' | 'TOUTES';

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './center-demandes.component.html',
  styles: [`.request{display:flex;justify-content:space-between;gap:12px;padding:14px 0;border-bottom:1px solid var(--border)}.request:last-child{border-bottom:0}.request p{margin:4px 0 0;color:var(--muted);font-size:13px}`]
})
export class CenterDemandesComponent {
  readonly demandeLabel = demandeLabel;
  readonly demandeClass = demandeClass;
  readonly urgenceClass = urgenceClass;
  readonly filtres: { value: FiltreDemandes; label: string }[] = [
    { value: 'EN_ATTENTE', label: 'En attente' },
    { value: 'TRAITEES', label: 'Traitées' },
    { value: 'TOUTES', label: 'Toutes' }
  ];

  filtre: FiltreDemandes = 'EN_ATTENTE';

  /** Demande en cours d'acceptation (saisie du nombre de poches fournies). */
  acceptId: number | null = null;
  unites = 1;

  constructor(public store: CenterStore) {}

  get demandes(): DemandeDeSang[] {
    const all = this.store.demandes;
    if (this.filtre === 'EN_ATTENTE') return all.filter(d => d.statut === 'EN_ATTENTE');
    if (this.filtre === 'TRAITEES') return all.filter(d => d.statut !== 'EN_ATTENTE');
    return all;
  }

  startAccept(d: DemandeDeSang) {
    this.acceptId = d.id;
    this.unites = Math.min(d.quantite, this.store.stockDispo(d.groupeSanguin)) || d.quantite;
  }

  accepter(d: DemandeDeSang) {
    this.store.repondre(d, 'ACCEPTEE', this.unites, () => this.acceptId = null);
  }

  refuser(d: DemandeDeSang) {
    if (!confirm(`Refuser la demande #${d.id} ?`)) return;
    this.store.repondre(d, 'REFUSEE', undefined);
  }
}
