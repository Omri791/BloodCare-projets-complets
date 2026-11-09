import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CenterStore } from '../../center.store';
import { StatCardComponent } from '../../../../shared/components/stat-card/stat-card.component';
import { rdvActif, urgenceClass } from '../../../../shared/utils/status.utils';

@Component({
  standalone: true,
  imports: [DatePipe, RouterLink, StatCardComponent],
  templateUrl: './center-overview.component.html'
})
export class CenterOverviewComponent {
  readonly urgenceClass = urgenceClass;

  constructor(public store: CenterStore) {}

  get stocksBas() { return this.store.stockLignes.filter(s => s.niveau !== 'Normal'); }
  get demandesEnAttente() { return this.store.demandes.filter(d => d.statut === 'EN_ATTENTE').slice(0, 5); }
  get prochainsRdv() { return this.store.rendezVous.filter(r => rdvActif(r.statut)).slice(0, 5); }
}
