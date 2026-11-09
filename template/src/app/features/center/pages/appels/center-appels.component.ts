import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CenterStore } from '../../center.store';
import { Donneur } from '../../../../shared/models/backend.model';
import { GROUPES_SANGUINS } from '../../../../shared/constants/blood-groups';
import { appelClass, appelLabel } from '../../../../shared/utils/status.utils';

/**
 * Appels aux donneurs. Pré-rempli depuis les autres pages :
 *  ?groupe=O-   (stock bas)  -> appel aux donneurs de ce groupe
 *  ?donneur=12  (donneurs)   -> appel individuel
 */
@Component({
  standalone: true,
  imports: [FormsModule, DatePipe, RouterLink],
  templateUrl: './center-appels.component.html'
})
export class CenterAppelsComponent {
  readonly groupes = GROUPES_SANGUINS;
  readonly appelLabel = appelLabel;
  readonly appelClass = appelClass;

  form = { groupeSanguin: '', message: '' };
  private donneurId: number | null = null;

  constructor(public store: CenterStore, private route: ActivatedRoute, private router: Router) {
    route.queryParamMap.pipe(takeUntilDestroyed()).subscribe(p => {
      this.donneurId = p.has('donneur') ? Number(p.get('donneur')) : null;
      this.form = { groupeSanguin: p.get('groupe') ?? '', message: '' };
    });
  }

  /** Les donneurs peuvent arriver après la page (chargement du store) : on les retrouve à la volée. */
  get donneur(): Donneur | null {
    return this.donneurId == null ? null : this.store.donneurs.find(d => d.id === this.donneurId) ?? null;
  }

  envoyer() {
    const groupe = this.donneur ? this.donneur.groupeSanguin ?? '' : this.form.groupeSanguin;
    this.store.lancerAppel(groupe, this.form.message, this.donneur, () => {
      this.form = { groupeSanguin: '', message: '' };
      this.appelATous();
    });
  }

  /** Retour au formulaire d'appel général (efface ?donneur / ?groupe). */
  appelATous() {
    this.router.navigate([], { relativeTo: this.route, queryParams: {} });
  }
}
