import { Component } from '@angular/core';
@Component({standalone:true,templateUrl:'./center-dashboard.component.html',styles:[`.stock{display:grid;grid-template-columns:55px 1fr auto;align-items:center;gap:10px;padding:10px 0;border-bottom:1px solid var(--border)}.type{font-weight:800;color:var(--primary)}.request{display:flex;justify-content:space-between;gap:12px;padding:14px 0;border-bottom:1px solid var(--border)}.request p{margin:4px 0 0;color:var(--muted);font-size:13px}`]})
export class CenterDashboardComponent {
  stock=[['O+',42,'Normal'],['O-',8,'Faible'],['A+',31,'Normal'],['A-',12,'Faible'],['B+',27,'Normal'],['B-',9,'Faible'],['AB+',16,'Normal'],['AB-',5,'Critique']];
  requests=[{id:201,hospital:'Hôpital Charles Nicolle',type:'O-',qty:6,urgency:'CRITIQUE'},{id:202,hospital:'Hôpital La Rabta',type:'A+',qty:3,urgency:'URGENT'},{id:203,hospital:'Hôpital Mongi Slim',type:'B+',qty:2,urgency:'NORMAL'}];
  launchCall(){alert('Appel aux donneurs lancé (mode template).')}
  respond(id:number){alert('Réponse à la demande #'+id+' (mode template).')}
}
