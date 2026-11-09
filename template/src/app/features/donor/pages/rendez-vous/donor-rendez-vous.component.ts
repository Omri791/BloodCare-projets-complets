import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DonorStore } from '../../donor.store';
import { RendezVous } from '../../../../shared/models/backend.model';
import { nowLocalInput } from '../../../../shared/utils/date.utils';
import { rdvActif, rdvClass, rdvLabel } from '../../../../shared/utils/status.utils';

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './donor-rendez-vous.component.html'
})
export class DonorRendezVousComponent {
  readonly rdvClass = rdvClass;
  readonly minDate = nowLocalInput();

  showForm = false;
  form = { centreId: null as number | null, date: '' };

  constructor(public store: DonorStore) {}

  /** Côté donneur, un rendez-vous planifié attend la confirmation du centre. */
  rdvLabel(s: string) { return s === 'PLANIFIE' ? 'EN ATTENTE DE CONFIRMATION' : rdvLabel(s); }

  actif(r: RendezVous) { return rdvActif(r.statut); }

  prendre() {
    if (!this.form.centreId) return;
    this.store.prendreRendezVous(this.form.centreId, this.form.date, () => {
      this.showForm = false;
      this.form = { centreId: null, date: '' };
    });
  }

  annuler(r: RendezVous) {
    if (!confirm('Annuler ce rendez-vous ?')) return;
    this.store.annuler(r);
  }
}
