import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { DonorStore } from '../../donor.store';
import { AppelDon, Creneau } from '../../../../shared/models/backend.model';

@Component({
  standalone: true,
  imports: [DatePipe],
  templateUrl: './donor-appels.component.html'
})
export class DonorAppelsComponent {
  /** Appel accepté : on affiche les créneaux du centre pour en choisir un. */
  appelChoisi: AppelDon | null = null;
  creneaux: Creneau[] = [];
  loadingCreneaux = false;

  constructor(public store: DonorStore) {}

  accepter(a: AppelDon) {
    this.appelChoisi = a;
    this.creneaux = [];
    this.loadingCreneaux = true;
    this.store.error = '';
    this.store.creneauxAppel(a).subscribe({
      next: c => { this.creneaux = c; this.loadingCreneaux = false; },
      error: (e: HttpErrorResponse) => { this.loadingCreneaux = false; this.store.error = e.error?.message || 'Impossible de charger les créneaux.'; }
    });
  }

  choisirCreneau(c: Creneau) {
    if (!this.appelChoisi) return;
    this.store.accepterAppel(this.appelChoisi, c, () => this.appelChoisi = null);
  }

  refuser(a: AppelDon) {
    if (!confirm('Refuser cet appel ?')) return;
    this.store.refuserAppel(a, () => { if (this.appelChoisi?.id === a.id) this.appelChoisi = null; });
  }
}
