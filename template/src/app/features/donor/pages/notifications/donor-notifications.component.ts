import { Component } from '@angular/core';
import { DonorStore } from '../../donor.store';
import { NotificationListComponent } from '../../../../shared/components/notification-list/notification-list.component';

@Component({
  standalone: true,
  imports: [NotificationListComponent],
  template: `
    <div class="page-title"><div><h1>Notifications</h1><p>{{store.nonLues}} non lue(s)</p></div></div>
    <div class="card"><app-notification-list [notifications]="store.notifications" /></div>
  `
})
export class DonorNotificationsComponent {
  constructor(public store: DonorStore) {}
}
