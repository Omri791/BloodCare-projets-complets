import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CenterStore } from '../../center.store';
import { Creneau } from '../../../../shared/models/backend.model';
import { isPast, nowLocalInput } from '../../../../shared/utils/date.utils';

@Component({
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './center-creneaux.component.html'
})
export class CenterCreneauxComponent {
  readonly isPast = isPast;
  readonly minDate = nowLocalInput();

  form = { date: '', capacite: 1 };

  constructor(public store: CenterStore) {}

  ajouter() {
    this.store.ajouterCreneau(this.form.date, this.form.capacite, () => this.form = { date: '', capacite: this.form.capacite });
  }

  supprimer(c: Creneau) {
    if (!confirm('Supprimer ce créneau ?')) return;
    this.store.supprimerCreneau(c);
  }
}
