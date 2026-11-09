import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CenterStore } from '../../center.store';

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './center-stock.component.html',
  styles: [`.stock{display:grid;grid-template-columns:55px 1fr auto;align-items:center;gap:10px;padding:12px 0;border-bottom:1px solid var(--border)}.stock:last-child{border-bottom:0}`]
})
export class CenterStockComponent {
  /** Mode édition : quantités saisies par groupe. */
  gestion = false;
  edit: Record<string, number> = {};

  constructor(public store: CenterStore) {}

  ouvrirGestion() {
    this.edit = Object.fromEntries(this.store.stockLignes.map(s => [s.groupe, s.quantite]));
    this.gestion = true;
  }

  ajuster(g: string, delta: number) {
    this.edit[g] = Math.max(0, (this.edit[g] ?? 0) + delta);
  }

  enregistrer(g: string) {
    this.store.definirStock(g, this.edit[g]);
  }
}
