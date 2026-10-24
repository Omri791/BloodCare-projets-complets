import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DonationService } from '../donation.service';
import { Donation } from '../donation.model';

@Component({standalone:true,imports:[RouterLink],templateUrl:'./donation-detail.component.html'})
export class DonationDetailComponent{donation?:Donation;constructor(r:ActivatedRoute,s:DonationService){s.getById(Number(r.snapshot.paramMap.get('id'))).subscribe(x=>this.donation=x)}}
