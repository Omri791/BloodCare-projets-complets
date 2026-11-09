import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DashboardLayoutComponent } from '../../core/layout/dashboard-layout/dashboard-layout.component';
import { SidebarItem } from '../../core/layout/sidebar/sidebar-item.model';
import { HospitalStore } from './hospital.store';

/** Espace Hôpital : menu latéral + page sélectionnée. */
@Component({
  standalone: true,
  imports: [RouterOutlet, DashboardLayoutComponent],
  providers: [HospitalStore],
  template: `<app-dashboard-layout roleLabel="Hôpital" [menu]="menu" [store]="store"><router-outlet /></app-dashboard-layout>`
})
export class HospitalLayoutComponent implements OnInit {
  readonly menu: SidebarItem[] = [
    { label: "Vue d'ensemble", path: '/dashboard/hospital' },
    { label: 'Nouvelle demande', path: '/dashboard/hospital/nouvelle-demande' },
    { label: 'Mes demandes', path: '/dashboard/hospital/demandes', badge: () => this.store.enAttente },
    { label: 'Notifications', path: '/dashboard/hospital/notifications', badge: () => this.store.nonLues }
  ];

  constructor(public store: HospitalStore) {}

  ngOnInit() { this.store.load(); }
}
