import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({providedIn:'root'})
export class CenterService {
  // Remplacer progressivement les données mockées par les appels Spring Boot.
  // Exemple : return this.api.get<Type>('/center');
  list(): Observable<unknown[]> { return of([]); }
}
