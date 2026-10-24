import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Donation } from './donation.model';

@Injectable({providedIn:'root'})
export class DonationService {
  private donations:Donation[]=[
    {id:1,donorName:'Amine Ben Ali',bloodType:'O+',centerName:'Centre Tunis',date:'2026-09-27',status:'COMPLETED',quantity:450},
    {id:2,donorName:'Sara Trabelsi',bloodType:'A+',centerName:'Centre Nabeul',date:'2026-09-29',status:'ACCEPTED',quantity:450},
    {id:3,donorName:'Yassine Gharbi',bloodType:'O-',centerName:'Centre Sousse',date:'2026-10-03',status:'PENDING',quantity:450}
  ];
  getAll():Observable<Donation[]>{return of(this.donations)}
  getById(id:number):Observable<Donation|undefined>{return of(this.donations.find(d=>d.id===id))}
}
