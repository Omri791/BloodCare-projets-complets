import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

/**
 * L'inscription est faite sur Keycloak. Un compte créé ici est un compte Donneur.
 * Les comptes Hôpital / Centre de don / Administrateur sont créés par l'administrateur.
 */
@Component({standalone:true,imports:[RouterLink],templateUrl:'./signup.component.html',styleUrl:'./signup.component.css'})
export class SignupComponent {
  constructor(public auth:AuthService){}
}
