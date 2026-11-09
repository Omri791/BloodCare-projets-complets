import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AdminStore } from '../../admin.store';

const EMPTY_HOPITAL = { nom: '', email: '', ville: '', adresse: '', type: 'Public', capaciteLits: 100 };

/** Hôpitaux partenaires. ?nouveau=1 ouvre directement le formulaire de création. */
@Component({
  standalone: true,
  imports: [FormsModule],
  templateUrl: './admin-hopitaux.component.html'
})
export class AdminHopitauxComponent {
  showForm: boolean;
  newHopital = { ...EMPTY_HOPITAL };

  constructor(public store: AdminStore, route: ActivatedRoute) {
    this.showForm = route.snapshot.queryParamMap.has('nouveau');
  }

  creer() {
    this.store.creerHopital({ ...this.newHopital }, () => {
      this.showForm = false;
      this.newHopital = { ...EMPTY_HOPITAL };
    });
  }
}
