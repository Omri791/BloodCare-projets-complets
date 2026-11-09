import { Component } from '@angular/core';
import { AdminStore } from '../../admin.store';

@Component({
  standalone: true,
  templateUrl: './admin-centres.component.html'
})
export class AdminCentresComponent {
  constructor(public store: AdminStore) {}
}
