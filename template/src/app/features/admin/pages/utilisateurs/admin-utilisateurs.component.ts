import { Component } from '@angular/core';
import { AdminStore } from '../../admin.store';
import { BackendRole, Utilisateur } from '../../../../shared/models/backend.model';
import { roleLabel } from '../../../../shared/utils/status.utils';

@Component({
  standalone: true,
  templateUrl: './admin-utilisateurs.component.html'
})
export class AdminUtilisateursComponent {
  readonly roleLabel = roleLabel;
  readonly filters: { value: BackendRole | 'ALL'; label: string }[] = [
    { value: 'ALL', label: 'Tous' },
    { value: 'HOPITAL', label: 'Hôpitaux' },
    { value: 'CENTRE_DON', label: 'Centres' },
    { value: 'DONNEUR', label: 'Donneurs' },
    { value: 'ADMIN', label: 'Admins' }
  ];

  filter: BackendRole | 'ALL' = 'ALL';

  constructor(public store: AdminStore) {}

  get filtered() { return this.filter === 'ALL' ? this.store.users : this.store.users.filter(u => u.role === this.filter); }

  supprimer(u: Utilisateur) {
    if (!confirm(`Supprimer ${u.nom} (${u.email}) ?`)) return;
    this.store.supprimerUtilisateur(u);
  }
}
