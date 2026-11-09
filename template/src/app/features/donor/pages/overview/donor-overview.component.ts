import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DonorStore } from '../../donor.store';
import { StatCardComponent } from '../../../../shared/components/stat-card/stat-card.component';

@Component({
  standalone: true,
  imports: [DatePipe, RouterLink, StatCardComponent],
  templateUrl: './donor-overview.component.html'
})
export class DonorOverviewComponent {
  constructor(public store: DonorStore) {}
}
