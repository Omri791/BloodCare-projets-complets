import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DashboardLayoutComponent } from '../../core/layout/dashboard-layout/dashboard-layout.component';
import { SidebarItem } from '../../core/layout/sidebar/sidebar-item.model';
import { DonorStore } from './donor.store';

/** Espace Donneur : menu latéral + page sélectionnée. */
@Component({
  standalone: true,
  imports: [RouterOutlet, DashboardLayoutComponent],
  providers: [DonorStore],
  template: `<app-dashboard-layout roleLabel="Donneur" [menu]="menu" [store]="store"><router-outlet /></app-dashboard-layout>`
})
export class DonorLayoutComponent implements OnInit {
  readonly menu: SidebarItem[] = [
    { label: "Vue d'ensemble", path: '/dashboard/donor' },
    { label: 'Mon profil', path: '/dashboard/donor/profil', badge: () => this.store.profilIncomplet ? 1 : 0 },
    { label: 'Appels des centres', path: '/dashboard/donor/appels', badge: () => this.store.appels.length },
    { label: 'Mes rendez-vous', path: '/dashboard/donor/rendez-vous' },
    { label: 'Historique des dons', path: '/dashboard/donor/historique' },
    { label: 'Notifications', path: '/dashboard/donor/notifications', badge: () => this.store.nonLues }
  ];

  constructor(public store: DonorStore) {}

  ngOnInit() { this.store.load(); }
}
