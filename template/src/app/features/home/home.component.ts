import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SectionTitleComponent } from '../../shared/components/section-title/section-title.component';
import { BloodTypeBadgeComponent } from '../../shared/components/blood-type-badge/blood-type-badge.component';

@Component({
  standalone: true,
  imports: [RouterLink, SectionTitleComponent, BloodTypeBadgeComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {}
