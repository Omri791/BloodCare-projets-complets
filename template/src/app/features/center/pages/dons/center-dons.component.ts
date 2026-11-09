import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { CenterStore } from '../../center.store';

@Component({
  standalone: true,
  imports: [DatePipe],
  templateUrl: './center-dons.component.html'
})
export class CenterDonsComponent {
  constructor(public store: CenterStore) {}
}
