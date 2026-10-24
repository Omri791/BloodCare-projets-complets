import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
export interface NotificationItem { id:number; title:string; message:string; read:boolean; date:string; }
@Injectable({providedIn:'root'})
export class NotificationService {
  getNotifications():Observable<NotificationItem[]> {
    return of([
      {id:1,title:'Nouvelle demande',message:'Une demande de sang O- a été reçue.',read:false,date:'Aujourd’hui'},
      {id:2,title:'Rendez-vous confirmé',message:'Votre rendez-vous est confirmé.',read:true,date:'Hier'}
    ]);
  }
}
