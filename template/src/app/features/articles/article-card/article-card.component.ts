import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Article } from '../article.model';

@Component({selector:'app-article-card',standalone:true,imports:[RouterLink],template:`<div class="card"><span>{{article.category}}</span><h3>{{article.title}}</h3><p>{{article.excerpt}}</p><a [routerLink]="['/articles',article.id]">Lire →</a></div>`})
export class ArticleCardComponent { @Input({required:true}) article!: Article; }
