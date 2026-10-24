import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ArticleService } from '../article.service';
import { Article } from '../article.model';

@Component({standalone:true,imports:[RouterLink],templateUrl:'./article-detail.component.html',styleUrl:'./article-detail.component.css'})
export class ArticleDetailComponent {
  article?: Article;
  constructor(route:ActivatedRoute,service:ArticleService){ const id=Number(route.snapshot.paramMap.get('id')); service.getById(id).subscribe(a=>this.article=a); }
}
