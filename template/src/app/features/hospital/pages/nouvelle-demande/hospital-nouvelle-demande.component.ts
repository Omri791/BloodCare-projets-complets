import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HospitalStore } from '../../hospital.store';
import { GROUPES_SANGUINS } from '../../../../shared/constants/blood-groups';

@Component({
  standalone: true,
  imports: [FormsModule],
  templateUrl: './hospital-nouvelle-demande.component.html'
})
export class HospitalNouvelleDemandeComponent {
  readonly groupes = GROUPES_SANGUINS;

  request = { groupeSanguin: 'O+', quantite: 2, urgence: 'URGENT', motif: '' };

  constructor(public store: HospitalStore, private router: Router) {}

  submit() {
    this.store.envoyerDemande({ ...this.request }, () => {
      this.store.keepFeedbackOnNextPage();
      this.router.navigate(['/dashboard/hospital/demandes']);
    });
  }
}
