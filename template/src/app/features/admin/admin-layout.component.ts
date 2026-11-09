import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DashboardLayoutComponent } from '../../core/layout/dashboard-layout/dashboard-layout.component';
import { SidebarItem } from '../../core/layout/sidebar/sidebar-item.model';
import { AdminStore } from './admin.store';

/** Espace Administration : menu latéral + page sélectionnée. */
@Component({
  standalone: true,
  imports: [RouterOutlet, DashboardLayoutComponent],
  providers: [AdminStore],
  template: `<app-dashboard-layout roleLabel="Administrateur" [menu]="menu" [store]="store"><router-outlet /></app-dashboard-layout>`
})
export class AdminLayoutComponent implements OnInit {
  readonly menu: SidebarItem[] = [
    { label: "Vue d'ensemble", path: '/dashboard/admin' },
    { label: 'Utilisateurs', path: '/dashboard/admin/utilisateurs' },
    { label: 'Hôpitaux', path: '/dashboard/admin/hopitaux' },
    { label: 'Centres de don', path: '/dashboard/admin/centres' },
    { label: 'Demandes en attente', path: '/dashboard/admin/demandes', badge: () => this.store.demandesEnAttente.length }
  ];

  constructor(public store: AdminStore) {}

  ngOnInit() { this.store.load(); }
}
