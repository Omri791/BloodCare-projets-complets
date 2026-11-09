import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HospitalStore } from '../../hospital.store';
import { demandeClass, demandeLabel, urgenceClass } from '../../../../shared/utils/status.utils';

@Component({
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './hospital-demandes.component.html'
})
export class HospitalDemandesComponent {
  readonly demandeLabel = demandeLabel;
  readonly demandeClass = demandeClass;
  readonly urgenceClass = urgenceClass;

  constructor(public store: HospitalStore) {}
}
