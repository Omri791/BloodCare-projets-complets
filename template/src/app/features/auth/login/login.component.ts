import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

/** La saisie email / mot de passe est faite sur la page Keycloak : cette page y redirige. */
@Component({standalone:true,imports:[RouterLink],templateUrl:'./login.component.html',styleUrl:'./login.component.css'})
export class LoginComponent implements OnInit {
  constructor(private auth:AuthService,private router:Router){}

  ngOnInit(){
    if(this.auth.isLoggedIn()) this.router.navigateByUrl(this.auth.dashboardLink());
    else this.login();
  }

  login(){ this.auth.login('/dashboard'); }
}
