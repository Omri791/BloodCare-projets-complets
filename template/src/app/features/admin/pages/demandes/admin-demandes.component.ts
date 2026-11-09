import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AdminStore } from '../../admin.store';
import { urgenceClass } from '../../../../shared/utils/status.utils';

@Component({
  standalone: true,
  imports: [DatePipe],
  templateUrl: './admin-demandes.component.html'
})
export class AdminDemandesComponent {
  readonly urgenceClass = urgenceClass;

  constructor(public store: AdminStore) {}
}
