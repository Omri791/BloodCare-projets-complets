import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../../shared/models/user.model';

/**
 * Protège une route.
 *  - non connecté  -> page de login Keycloak, puis retour sur la page demandée
 *  - mauvais rôle  -> redirigé vers son propre dashboard
 * Rôles autorisés : data: { roles: ['ADMIN'] } dans app.routes.ts
 */
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    auth.login(state.url);
    return false;
  }

  const allowed = route.data['roles'] as UserRole[] | undefined;
  const role = auth.role();
  if (allowed && (!role || !allowed.includes(role))) {
    return router.createUrlTree([auth.dashboardLink()]);
  }
  return true;
};

/** /dashboard -> dashboard correspondant au rôle (utilisé comme page de retour après login). */
export const dashboardRedirectGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.isLoggedIn()) {
    auth.login(state.url);
    return false;
  }
  return router.createUrlTree([auth.dashboardLink()]);
};
