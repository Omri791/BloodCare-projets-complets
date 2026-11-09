import { Component, Input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { NotificationDto } from '../../models/backend.model';

/** Liste des notifications ; les non lues sont en gras. */
@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [DatePipe],
  template: `
    @if (notifications.length === 0) { <p class="muted">Aucune notification.</p> }
    @for (n of notifications; track n.id) {
      <p class="item-row" [class.unread]="!n.lu"><span class="muted">{{n.date | date:'dd/MM/yyyy'}}</span> — {{n.message}}</p>
    }
  `,
  styles: [`p{margin:0}.unread{font-weight:700}`]
})
export class NotificationListComponent {
  @Input() notifications: NotificationDto[] = [];
}
