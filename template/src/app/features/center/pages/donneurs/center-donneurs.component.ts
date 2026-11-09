import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CenterStore } from '../../center.store';
import { Donneur } from '../../../../shared/models/backend.model';
import { GROUPES_SANGUINS } from '../../../../shared/constants/blood-groups';

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe, RouterLink],
  templateUrl: './center-donneurs.component.html'
})
export class CenterDonneursComponent {
  readonly groupes = GROUPES_SANGUINS;

  filtre = { texte: '', groupe: '', disponibles: false };

  constructor(public store: CenterStore) {}

  get donneurs(): Donneur[] {
    const t = this.filtre.texte.trim().toLowerCase();
    return this.store.donneurs.filter(d =>
      (!t || d.nom.toLowerCase().includes(t) || d.email.toLowerCase().includes(t) || (d.ville ?? '').toLowerCase().includes(t)) &&
      (!this.filtre.groupe || d.groupeSanguin === this.filtre.groupe) &&
      (!this.filtre.disponibles || d.disponibilite));
  }
}
