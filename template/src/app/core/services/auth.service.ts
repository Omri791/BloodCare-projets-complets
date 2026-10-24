import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { User, UserRole } from '../../shared/models/user.model';
import { APP_CONFIG } from '../config/app-config';

/** Rôles realm Keycloak -> rôles utilisés par le front (ordre = priorité). */
const ROLE_MAP: [string, UserRole][] = [
  ['ADMIN', 'ADMIN'],
  ['HOPITAL', 'HOSPITAL'],
  ['CENTRE_DON', 'CENTER'],
  ['DONNEUR', 'DONOR']
];

@Injectable({ providedIn: 'root' })
export class AuthService {
  private keycloak = new Keycloak(APP_CONFIG.keycloak);

  /** Appelé au démarrage de l'application (voir app.config.ts). */
  async init(): Promise<void> {
    try {
      await this.keycloak.init({
        onLoad: 'check-sso',                       // reconnecte sans forcer le login sur les pages publiques
        pkceMethod: 'S256',
        silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`
      });
    } catch (e) {
      // Keycloak injoignable : les pages publiques restent accessibles
      console.error('Keycloak init error', e);
    }
  }

  isLoggedIn(): boolean { return this.keycloak.authenticated === true; }

  /** Redirige vers la page de connexion Keycloak, puis revient sur `path`. */
  login(path = '/dashboard'): Promise<void> {
    return this.keycloak.login({ redirectUri: window.location.origin + path });
  }

  logout(): Promise<void> {
    return this.keycloak.logout({ redirectUri: window.location.origin });
  }

  /** Token valide (rafraîchi s'il expire dans moins de 30 s). */
  async getToken(): Promise<string | undefined> {
    if (!this.isLoggedIn()) return undefined;
    try {
      await this.keycloak.updateToken(30);
    } catch {
      await this.login(window.location.pathname);
      return undefined;
    }
    return this.keycloak.token;
  }

  /** Page d'inscription Keycloak (compte sans rôle applicatif = Donneur). */
  register(path = '/dashboard'): Promise<void> {
    return this.keycloak.register({ redirectUri: window.location.origin + path });
  }

  /** Rôle applicatif ; un compte connecté sans rôle (inscription libre) est un Donneur. */
  role(): UserRole | null {
    if (!this.isLoggedIn()) return null;
    const roles: string[] = this.keycloak.tokenParsed?.realm_access?.roles ?? [];
    return ROLE_MAP.find(([kc]) => roles.includes(kc))?.[1] ?? 'DONOR';
  }

  user(): User | null {
    const t = this.keycloak.tokenParsed;
    const role = this.role();
    if (!t || !role) return null;
    return {
      firstName: t['given_name'] ?? t['preferred_username'] ?? '',
      lastName: t['family_name'] ?? '',
      email: t['email'] ?? '',
      role
    };
  }

  dashboardLink(): string {
    switch (this.role()) {
      case 'ADMIN': return '/dashboard/admin';
      case 'HOSPITAL': return '/dashboard/hospital';
      case 'CENTER': return '/dashboard/center';
      case 'DONOR': return '/dashboard/donor';
      default: return '/';
    }
  }
}
