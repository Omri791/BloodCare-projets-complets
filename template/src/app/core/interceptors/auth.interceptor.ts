import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { from, switchMap } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { APP_CONFIG } from '../config/app-config';

/** Ajoute "Authorization: Bearer <token Keycloak>" à tous les appels vers l'API Spring Boot. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(APP_CONFIG.apiUrl)) return next(req);
  const auth = inject(AuthService);
  return from(auth.getToken()).pipe(
    switchMap(token => next(token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req))
  );
};
