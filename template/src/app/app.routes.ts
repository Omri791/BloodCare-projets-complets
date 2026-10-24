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

  // Dashboards
  { path: 'dashboard', canActivate: [dashboardRedirectGuard], children: [] },
  {
    path: 'dashboard/hospital',
    canActivate: [authGuard],
    data: { roles: ['HOSPITAL'] },
    loadComponent: () => import('./features/hospital/hospital-dashboard.component').then(m => m.HospitalDashboardComponent)
  },
  {
    path: 'dashboard/center',
    canActivate: [authGuard],
    data: { roles: ['CENTER'] },
    loadComponent: () => import('./features/center/center-dashboard.component').then(m => m.CenterDashboardComponent)
  },
  {
    path: 'dashboard/donor',
    canActivate: [authGuard],
    data: { roles: ['DONOR'] },
    loadComponent: () => import('./features/donor/donor-dashboard.component').then(m => m.DonorDashboardComponent)
  },
  {
    path: 'dashboard/admin',
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] },
    loadComponent: () => import('./features/admin/admin-dashboard.component').then(m => m.AdminDashboardComponent)
  },

  { path: '**', redirectTo: '' }
];
