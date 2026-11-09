import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SidebarItem } from './sidebar-item.model';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  @Input({ required: true }) roleLabel!: string;
  @Input() name = '';
  @Input({ required: true }) items!: SidebarItem[];

  constructor(public auth: AuthService) {}

  /** La première entrée (vue d'ensemble) ne doit être active que sur sa propre URL. */
  exact(item: SidebarItem) { return item === this.items[0]; }

  get initial() { return (this.name || this.roleLabel).charAt(0).toUpperCase(); }
}
