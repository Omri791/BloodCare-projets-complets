import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { DonorStore } from '../../donor.store';

@Component({
  standalone: true,
  imports: [DatePipe],
  templateUrl: './donor-historique.component.html'
})
export class DonorHistoriqueComponent {
  constructor(public store: DonorStore) {}
}
