import { Component } from '@angular/core';
@Component({standalone:true,templateUrl:'./donor-dashboard.component.html'})
export class DonorDashboardComponent {
  available=true;
  appointments=[{date:'05 Oct 2026',time:'09:30',center:'Centre de don Nabeul',status:'CONFIRMÉ'},{date:'18 Nov 2026',time:'10:00',center:'Centre de don Tunis',status:'PLANIFIÉ'}];
  toggle(){this.available=!this.available}
}
