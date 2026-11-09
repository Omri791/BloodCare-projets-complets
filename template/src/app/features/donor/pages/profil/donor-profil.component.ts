import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DonorStore } from '../../donor.store';
import { Donneur } from '../../../../shared/models/backend.model';
import { GROUPES_SANGUINS } from '../../../../shared/constants/blood-groups';

@Component({
  standalone: true,
  imports: [FormsModule],
  templateUrl: './donor-profil.component.html'
})
export class DonorProfilComponent {
  readonly groupes = GROUPES_SANGUINS;

  private copie: Partial<Donneur> | null = null;

  constructor(public store: DonorStore) {}

  /** Copie du profil pour le formulaire, créée dès que le donneur est chargé. */
  get profil(): Partial<Donneur> | null {
    if (!this.copie && this.store.donneur) this.copie = this.store.profilFrom(this.store.donneur);
    return this.copie;
  }

  enregistrer() {
    // la disponibilité se change avec son propre bouton : on garde la valeur actuelle
    if (this.copie) this.store.enregistrerProfil({ ...this.copie, disponibilite: this.store.donneur?.disponibilite });
  }

  annuler() {
    this.copie = null;
  }
}
