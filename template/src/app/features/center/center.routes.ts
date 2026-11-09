import { Routes } from '@angular/router';
import { CenterLayoutComponent } from './center-layout.component';
import { CenterOverviewComponent } from './pages/overview/center-overview.component';
import { CenterStockComponent } from './pages/stock/center-stock.component';
import { CenterDemandesComponent } from './pages/demandes/center-demandes.component';
import { CenterRendezVousComponent } from './pages/rendez-vous/center-rendez-vous.component';
import { CenterCreneauxComponent } from './pages/creneaux/center-creneaux.component';
import { CenterAppelsComponent } from './pages/appels/center-appels.component';
import { CenterDonneursComponent } from './pages/donneurs/center-donneurs.component';
import { CenterDonsComponent } from './pages/dons/center-dons.component';

/** /dashboard/center/... */
export const CENTER_ROUTES: Routes = [
  {
    path: '',
    component: CenterLayoutComponent,
    children: [
      { path: '', component: CenterOverviewComponent },
      { path: 'stock', component: CenterStockComponent },
      { path: 'demandes', component: CenterDemandesComponent },
      { path: 'rendez-vous', component: CenterRendezVousComponent },
      { path: 'creneaux', component: CenterCreneauxComponent },
      { path: 'appels', component: CenterAppelsComponent },
      { path: 'donneurs', component: CenterDonneursComponent },
      { path: 'dons', component: CenterDonsComponent }
    ]
  }
];
