import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-section-title',
  standalone: true,
  template: `<div class="title"><span>{{eyebrow}}</span><h2>{{title}}</h2><p>{{description}}</p></div>`,
  styles: [`.title{text-align:center;margin-bottom:28px}.title span{color:var(--primary);font-size:12px;text-transform:uppercase;font-weight:800;letter-spacing:1.5px}.title h2{font-size:30px;margin:7px 0}.title p{color:var(--muted);margin:0}`]
})
export class SectionTitleComponent {
  @Input() eyebrow = '';
  @Input() title = '';
  @Input() description = '';
}
