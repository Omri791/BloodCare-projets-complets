import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { TopBarComponent } from './core/layout/top-bar/top-bar.component';
import { FooterComponent } from './core/layout/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, TopBarComponent, FooterComponent],
  template: `
    <app-top-bar />
    <app-navbar />
    <main><router-outlet /></main>
    <app-footer />
  `
})
export class AppComponent {}
