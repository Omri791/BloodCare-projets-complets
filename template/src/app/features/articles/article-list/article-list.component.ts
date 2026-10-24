import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ArticleService } from '../article.service';
import { Article } from '../article.model';

@Component({
  standalone:true, imports:[RouterLink],
  templateUrl:'./article-list.component.html',
  styleUrl:'./article-list.component.css'
})
export class ArticleListComponent {
  articles: Article[] = [];
  constructor(service: ArticleService){ service.getAll().subscribe(a=>this.articles=a); }
}
