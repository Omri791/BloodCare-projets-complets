import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Article } from './article.model';

@Injectable({providedIn:'root'})
export class ArticleService {
  private articles: Article[] = [
    {id:1,title:'Pourquoi donner son sang ?',excerpt:'Comprendre l’importance du don régulier.',content:'Le don de sang contribue directement à la prise en charge de nombreux patients. Un don peut aider plusieurs personnes grâce à la séparation des composants sanguins.',category:'Sensibilisation',date:'2026-09-10',image:''},
    {id:2,title:'Les groupes sanguins expliqués',excerpt:'A+, O-, B+ : que signifient les groupes sanguins ?',content:'Les groupes sanguins sont déterminés par des antigènes présents à la surface des globules rouges. Leur compatibilité est essentielle lors des transfusions.',category:'Santé',date:'2026-08-22',image:''},
    {id:3,title:'Bien se préparer avant un don',excerpt:'Quelques conseils simples avant votre rendez-vous.',content:'Dormez suffisamment, mangez normalement et hydratez-vous. Suivez toujours les consignes données par le personnel du centre de don.',category:'Conseils',date:'2026-08-04',image:''}
  ];
  getAll(): Observable<Article[]> { return of(this.articles); }
  getById(id:number): Observable<Article|undefined> { return of(this.articles.find(a=>a.id===id)); }
}
