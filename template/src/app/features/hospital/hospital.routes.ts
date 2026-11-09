import { Routes } from '@angular/router';
import { HospitalLayoutComponent } from './hospital-layout.component';
import { HospitalOverviewComponent } from './pages/overview/hospital-overview.component';
import { HospitalNouvelleDemandeComponent } from './pages/nouvelle-demande/hospital-nouvelle-demande.component';
import { HospitalDemandesComponent } from './pages/demandes/hospital-demandes.component';
import { HospitalNotificationsComponent } from './pages/notifications/hospital-notifications.component';

/** /dashboard/hospital/... */
export const HOSPITAL_ROUTES: Routes = [
  {
    path: '',
    component: HospitalLayoutComponent,
    children: [
      { path: '', component: HospitalOverviewComponent },
      { path: 'nouvelle-demande', component: HospitalNouvelleDemandeComponent },
      { path: 'demandes', component: HospitalDemandesComponent },
      { path: 'notifications', component: HospitalNotificationsComponent }
    ]
  }
];
