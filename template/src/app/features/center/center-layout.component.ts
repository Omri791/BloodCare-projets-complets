import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DashboardLayoutComponent } from '../../core/layout/dashboard-layout/dashboard-layout.component';
import { SidebarItem } from '../../core/layout/sidebar/sidebar-item.model';
import { CenterStore } from './center.store';

/** Espace Centre de don : menu latéral + page sélectionnée. */
@Component({
  standalone: true,
  imports: [RouterOutlet, DashboardLayoutComponent],
  providers: [CenterStore],
  template: `<app-dashboard-layout roleLabel="Centre de don" [menu]="menu" [store]="store"><router-outlet /></app-dashboard-layout>`
})
export class CenterLayoutComponent implements OnInit {
  readonly menu: SidebarItem[] = [
    { label: "Vue d'ensemble", path: '/dashboard/center' },
    { label: 'Stock sanguin', path: '/dashboard/center/stock', badge: () => this.store.stocksCritiques },
    { label: 'Demandes hôpitaux', path: '/dashboard/center/demandes', badge: () => this.store.enAttente },
    { label: 'Rendez-vous', path: '/dashboard/center/rendez-vous', badge: () => this.store.rdvAConfirmer },
    { label: 'Créneaux', path: '/dashboard/center/creneaux' },
    { label: 'Appels aux donneurs', path: '/dashboard/center/appels' },
    { label: 'Donneurs', path: '/dashboard/center/donneurs' },
    { label: 'Dons enregistrés', path: '/dashboard/center/dons' }
  ];

  constructor(public store: CenterStore) {}

  ngOnInit() { this.store.load(); }
}
