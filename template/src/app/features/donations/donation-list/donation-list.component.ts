import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DonationService } from '../donation.service';
import { Donation } from '../donation.model';
import { BloodTypeBadgeComponent } from '../../../shared/components/blood-type-badge/blood-type-badge.component';

@Component({standalone:true,imports:[RouterLink,BloodTypeBadgeComponent],templateUrl:'./donation-list.component.html'})
export class DonationListComponent { donations:Donation[]=[]; constructor(s:DonationService){s.getAll().subscribe(x=>this.donations=x)} }
