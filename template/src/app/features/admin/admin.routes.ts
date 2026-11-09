import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './admin-layout.component';
import { AdminOverviewComponent } from './pages/overview/admin-overview.component';
import { AdminUtilisateursComponent } from './pages/utilisateurs/admin-utilisateurs.component';
import { AdminHopitauxComponent } from './pages/hopitaux/admin-hopitaux.component';
import { AdminCentresComponent } from './pages/centres/admin-centres.component';
import { AdminDemandesComponent } from './pages/demandes/admin-demandes.component';

/** /dashboard/admin/... */
export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: '', component: AdminOverviewComponent },
      { path: 'utilisateurs', component: AdminUtilisateursComponent },
      { path: 'hopitaux', component: AdminHopitauxComponent },
      { path: 'centres', component: AdminCentresComponent },
      { path: 'demandes', component: AdminDemandesComponent }
    ]
  }
];
