import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Base des "stores" de chaque espace (admin, hôpital, centre, donneur).
 * Un store est fourni par le layout de l'espace : toutes ses pages partagent
 * donc les mêmes données, chargées une seule fois.
 */
export abstract class DashboardStore {
  loading = true;
  error = '';
  message = '';
  saving = false;

  /** true quand le profil de l'utilisateur connecté est chargé. */
  abstract get ready(): boolean;

  /** Nom affiché dans le menu latéral. */
  abstract get displayName(): string;

  abstract load(): void;

  private keepFeedback = false;

  /** Efface les messages (appelé à chaque changement de page). */
  clearFeedback() {
    if (this.keepFeedback) { this.keepFeedback = false; return; }
    this.message = '';
    if (this.ready) this.error = '';
  }

  /** À appeler avant une redirection faite après une action, pour garder son message de succès. */
  keepFeedbackOnNextPage() { this.keepFeedback = true; }

  /** Lance une action : gère `saving`, les messages d'erreur, puis appelle onSuccess. */
  protected run<T>(obs: Observable<T>, onSuccess: (value: T) => void, errorMessage?: (e: HttpErrorResponse) => string) {
    if (this.saving) return;
    this.saving = true;
    this.error = '';
    this.message = '';
    obs.subscribe({
      next: v => { this.saving = false; onSuccess(v); },
      error: (e: HttpErrorResponse) => {
        this.saving = false;
        this.error = errorMessage?.(e) || e.error?.message || "L'opération a échoué.";
      }
    });
  }

  protected fail(msg: string) { this.error = msg; this.loading = false; }
}
