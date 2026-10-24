import { Component } from '@angular/core';

@Component({
  selector: 'app-top-bar',
  standalone: true,
  template: `
    <div class="topbar">
      <div class="topbar-inner">
        <span>📞 +216 71 000 000</span>
        <span>✉️ contact@bloodcare.tn</span>
        <span class="social">Facebook · Instagram · LinkedIn</span>
      </div>
    </div>
  `,
  styles: [`
    .topbar { background:#171b20; color:#dfe4e8; font-size:12px; }
    .topbar-inner { width:min(1180px,calc(100% - 40px)); margin:auto; min-height:34px; display:flex; align-items:center; gap:22px; }
    .social { margin-left:auto; }
    @media(max-width:650px){.topbar-inner{width:calc(100% - 24px);gap:10px}.social{display:none}}
  `]
})
export class TopBarComponent {}
