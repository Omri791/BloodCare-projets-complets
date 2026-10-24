import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
    <footer>
      <div class="footer-grid">
        <div><h3><span>♥</span> BloodCare</h3><p>Une plateforme digitale pour faciliter la gestion des dons de sang et connecter donneurs, centres et hôpitaux.</p></div>
        <div><h4>Plateforme</h4><p>Dons de sang</p><p>Articles santé</p><p>Centres de don</p></div>
        <div><h4>Contact</h4><p>+216 71 000 000</p><p>contact@bloodcare.tn</p><p>Tunis, Tunisie</p></div>
      </div>
      <div class="copy">© 2026 BloodCare — Système de gestion des dons de sang</div>
    </footer>
  `,
  styles: [`
    footer{background:#171b20;color:#cbd1d6;margin-top:30px}.footer-grid{width:min(1180px,calc(100% - 40px));margin:auto;padding:45px 0;display:grid;grid-template-columns:2fr 1fr 1fr;gap:35px}.footer-grid h3{color:white}.footer-grid h3 span{color:#e84d4d}.footer-grid h4{color:white;margin-bottom:12px}.footer-grid p{font-size:13px;color:#9da6ae}.copy{border-top:1px solid #2c3339;text-align:center;padding:18px;font-size:12px;color:#8d969f}@media(max-width:700px){.footer-grid{grid-template-columns:1fr}}
  `]
})
export class FooterComponent {}
