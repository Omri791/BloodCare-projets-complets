import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-blood-type-badge',
  standalone: true,
  template: `<span class="blood">{{type}}</span>`,
  styles: [`.blood{width:48px;height:48px;border-radius:50%;display:inline-grid;place-items:center;background:#fff0f0;border:2px solid #e7a2a2;color:var(--primary);font-weight:800;font-size:13px}`]
})
export class BloodTypeBadgeComponent { @Input() type = 'O+'; }
