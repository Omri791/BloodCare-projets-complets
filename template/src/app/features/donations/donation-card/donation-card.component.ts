import { Component, Input } from '@angular/core';
import { Donation } from '../donation.model';
@Component({selector:'app-donation-card',standalone:true,template:`<div class="card"><h3>{{donation.centerName}}</h3><p>{{donation.date}} · {{donation.bloodType}}</p><span>{{donation.status}}</span></div>`})
export class DonationCardComponent{@Input({required:true}) donation!:Donation}
