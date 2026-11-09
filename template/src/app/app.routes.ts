import { Routes } from '@angular/router';
import { authGuard, dashboardRedirectGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'articles',
    loadComponent: () => import('./features/articles/article-list/article-list.component').then(m => m.ArticleListComponent)
  },
  {
    path: 'articles/:id',
    loadComponent: () => import('./features/articles/article-detail/article-detail.component').then(m => m.ArticleDetailComponent)
  },
  {
    path: 'donations',
    loadComponent: () => import('./features/donations/donation-list/donation-list.component').then(m => m.DonationListComponent)
  },
  {
    path: 'donations/:id',
    loadComponent: () => import('./features/donations/donation-detail/donation-detail.component').then(m => m.DonationDetailComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'signup',
    loadComponent: () => import('./features/auth/signup/signup.component').then(m => m.SignupComponent)
  },
  {
    path: 'about',
    loadComponent: () => import('./features/about/about.component').then(m => m.AboutComponent)
  },
  {
    path: 'who-we-are',
    loadComponent: () => import('./features/who-we-are/who-we-are.component').then(m => m.WhoWeAreComponent)
  },
  {
    path: 'contact',
    loadComponent: () => import('./features/contact/contact.component').then(m => m.ContactComponent)
  },

  // Espaces : chaque rôle a son layout (menu latéral) et ses pages, voir features/<rôle>/<rôle>.routes.ts
  { path: 'dashboard', canActivate: [dashboardRedirectGuard], children: [] },
  {
    path: 'dashboard/hospital',
    canActivate: [authGuard],
    data: { roles: ['HOSPITAL'] },
    loadChildren: () => import('./features/hospital/hospital.routes').then(m => m.HOSPITAL_ROUTES)
  },
  {
    path: 'dashboard/center',
    canActivate: [authGuard],
    data: { roles: ['CENTER'] },
    loadChildren: () => import('./features/center/center.routes').then(m => m.CENTER_ROUTES)
  },
  {
    path: 'dashboard/donor',
    canActivate: [authGuard],
    data: { roles: ['DONOR'] },
    loadChildren: () => import('./features/donor/donor.routes').then(m => m.DONOR_ROUTES)
  },
  {
    path: 'dashboard/admin',
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] },
    loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
  },

  { path: '**', redirectTo: '' }
];
