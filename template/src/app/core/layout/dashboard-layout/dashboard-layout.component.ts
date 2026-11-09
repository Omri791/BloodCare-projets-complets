import { Component, Input } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { SidebarItem } from '../sidebar/sidebar-item.model';
import { DashboardStore } from '../../../shared/state/dashboard-store';

/**
 * Squelette commun aux espaces : menu latéral + messages + page courante (<ng-content>).
 * Utilisé par le layout de chaque rôle, qui fournit son menu et son store.
 */
@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [SidebarComponent],
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.css'
})
export class DashboardLayoutComponent {
  @Input({ required: true }) roleLabel!: string;
  @Input({ required: true }) menu!: SidebarItem[];
  @Input({ required: true }) store!: DashboardStore;

  private path = '';

  constructor(router: Router) {
    // un message de succès ne doit pas suivre l'utilisateur sur une autre page
    // (un simple changement de ?paramètres ne compte pas)
    router.events.pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd), takeUntilDestroyed())
      .subscribe(e => {
        const path = e.urlAfterRedirects.split('?')[0];
        if (path !== this.path) this.store?.clearFeedback();
        this.path = path;
      });
  }
}
