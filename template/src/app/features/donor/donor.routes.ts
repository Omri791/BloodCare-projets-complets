import { Routes } from '@angular/router';
import { DonorLayoutComponent } from './donor-layout.component';
import { DonorOverviewComponent } from './pages/overview/donor-overview.component';
import { DonorProfilComponent } from './pages/profil/donor-profil.component';
import { DonorAppelsComponent } from './pages/appels/donor-appels.component';
import { DonorRendezVousComponent } from './pages/rendez-vous/donor-rendez-vous.component';
import { DonorHistoriqueComponent } from './pages/historique/donor-historique.component';
import { DonorNotificationsComponent } from './pages/notifications/donor-notifications.component';

/** /dashboard/donor/... */
export const DONOR_ROUTES: Routes = [
  {
    path: '',
    component: DonorLayoutComponent,
    children: [
      { path: '', component: DonorOverviewComponent },
      { path: 'profil', component: DonorProfilComponent },
      { path: 'appels', component: DonorAppelsComponent },
      { path: 'rendez-vous', component: DonorRendezVousComponent },
      { path: 'historique', component: DonorHistoriqueComponent },
      { path: 'notifications', component: DonorNotificationsComponent }
    ]
  }
];
