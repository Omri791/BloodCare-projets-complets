import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AdminStore } from '../../admin.store';
import { StatCardComponent } from '../../../../shared/components/stat-card/stat-card.component';

@Component({
  standalone: true,
  imports: [RouterLink, StatCardComponent],
  templateUrl: './admin-overview.component.html'
})
export class AdminOverviewComponent {
  constructor(public store: AdminStore) {}
}
