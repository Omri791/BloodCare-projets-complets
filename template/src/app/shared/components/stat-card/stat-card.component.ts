import { Component, Input } from '@angular/core';

/** Carte de statistique (libellé, valeur) des vues d'ensemble. */
@Component({
  selector: 'app-stat-card',
  standalone: true,
  template: `<div class="card stat"><div><span>{{label}}</span><div class="number">{{value}}</div></div></div>`
})
export class StatCardComponent {
  @Input({ required: true }) label!: string;
  @Input() value: string | number | null | undefined = 0;
}
