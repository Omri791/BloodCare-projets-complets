import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CenterStore } from '../../center.store';
import { RendezVous } from '../../../../shared/models/backend.model';
import { GROUPES_SANGUINS } from '../../../../shared/constants/blood-groups';
import { nowLocalInput, today } from '../../../../shared/utils/date.utils';
import { rdvClass, rdvLabel } from '../../../../shared/utils/status.utils';

/** Rendez-vous du centre. ?email=... ouvre « Organiser un don » pour ce donneur. */
@Component({
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './center-rendez-vous.component.html'
})
export class CenterRendezVousComponent {
  readonly groupes = GROUPES_SANGUINS;
  readonly rdvLabel = rdvLabel;
  readonly rdvClass = rdvClass;
  readonly minDate = nowLocalInput();

  showRdvForm = false;
  rdvForm = { donneurEmail: '', date: '' };

  /** Rendez-vous pour lequel on enregistre le don. */
  donRdv: RendezVous | null = null;
  donForm = { dateDon: '', quantite: 450, groupeSanguin: 'O+' };

  constructor(public store: CenterStore, route: ActivatedRoute) {
    route.queryParamMap.pipe(takeUntilDestroyed()).subscribe(p => {
      const email = p.get('email');
      if (email) {
        this.rdvForm = { donneurEmail: email, date: '' };
        this.showRdvForm = true;
      }
    });
  }

  organiserDon() {
    this.store.organiserDon(this.rdvForm.donneurEmail, this.rdvForm.date, () => {
      this.showRdvForm = false;
      this.rdvForm = { donneurEmail: '', date: '' };
    });
  }

  changerStatut(r: RendezVous, statut: 'CONFIRME' | 'ANNULE' | 'ABSENT') {
    if (statut === 'ANNULE' && !confirm('Annuler ce rendez-vous ?')) return;
    this.store.changerStatut(r, statut);
  }

  startDon(r: RendezVous) {
    this.donRdv = r;
    this.donForm = { dateDon: today(), quantite: 450, groupeSanguin: r.donneur?.groupeSanguin ?? 'O+' };
  }

  enregistrerDon() {
    if (!this.donRdv) return;
    // le groupe n'est envoyé que s'il n'est pas encore connu pour ce donneur
    const body = { ...this.donForm, groupeSanguin: this.donRdv.donneur?.groupeSanguin ? undefined : this.donForm.groupeSanguin };
    this.store.enregistrerDon(this.donRdv, body, () => this.donRdv = null);
  }
}
