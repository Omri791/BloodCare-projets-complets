import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HospitalStore } from '../../hospital.store';
import { StatCardComponent } from '../../../../shared/components/stat-card/stat-card.component';
import { demandeClass, demandeLabel } from '../../../../shared/utils/status.utils';

@Component({
  standalone: true,
  imports: [DatePipe, RouterLink, StatCardComponent],
  templateUrl: './hospital-overview.component.html'
})
export class HospitalOverviewComponent {
  readonly demandeLabel = demandeLabel;
  readonly demandeClass = demandeClass;

  constructor(public store: HospitalStore) {}

  get recentes() { return this.store.demandes.slice(0, 5); }
}
