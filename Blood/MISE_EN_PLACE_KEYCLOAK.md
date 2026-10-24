# Keycloak — mise en place (BloodCare)

Login des 4 acteurs via Keycloak. Front connecté au backend pour **Administrateur** et **Hôpital**.
Centre de don et Donneur : le login fonctionne, leurs dashboards restent à brancher par les collègues.

## 1. Keycloak (port 8180)
```bash
cd keycloak
docker compose up -d
```
Le realm `blood` est importé automatiquement (rôles `ADMIN`, `HOPITAL`, `CENTRE_DON`, `DONNEUR`,
client public `blood-front` avec PKCE). Console admin : http://localhost:8180 (admin / admin).

| Email | Mot de passe | Acteur |
|---|---|---|
| admin@bloodcare.tn | Admin123! | Administrateur |
| hopital@bloodcare.tn | Hopital123! | Hôpital |
| centre@bloodcare.tn | Centre123! | Centre de don |
| donneur@bloodcare.tn | Donneur123! | Donneur |

Sans Docker : télécharger Keycloak 26, `bin\kc.bat start-dev --http-port=8180`, puis dans la console
*Create realm* -> importer `keycloak/blood-realm.json`.

## 2. Backend (port 8082) : `./mvnw spring-boot:run`
## 3. Front (port 4200) : `npm install` puis `npm start`

## Principes (pour toute l'équipe)
- Le mot de passe n'est plus géré par Spring : Keycloak authentifie, le backend valide le JWT.
- Compte Keycloak <-> table `utilisateur` : par **email**. Au 1er login, la fiche est créée selon le rôle
  (Admin / Hôpital / CentreDeDon / Donneur). Un compte sans rôle (inscription libre) devient Donneur.
- `GET /api/me` renvoie la fiche de l'utilisateur connecté (id, nom, email, `role`).
- Front : `AuthService` (login, logout, role), `authGuard` avec `data: { roles: [...] }` dans `app.routes.ts`,
  `authInterceptor` ajoute le token automatiquement à chaque appel de `ApiService`.
- Backend : les règles d'accès sont dans `config/SecurityConfig.java`. **Par défaut tout `/api/**` est réservé
  à l'ADMIN** ; chaque acteur ouvre ses endpoints dans la section "ACCÈS PAR ACTEUR" (exemple : Hôpital).
  Pour limiter un acteur à ses propres données : `@PreAuthorize("@access.selfOrAdmin(#id)")`.

## Modifications faites dans du code existant (le reste est inchangé)
- `Utilisateur` : ajout de `role` dans le JSON et d'un mot de passe de remplacement à la création
  (la colonne `mot_de_passe` reste obligatoire en base mais n'est plus utilisée).
- `HopitalController` : `@PreAuthorize` sur les routes `/{id}...`.
- `DemandeDeSangService.envoyer` : statut forcé à `EN_ATTENTE`, centre/unités remis à zéro.
- `UtilisateurRepository` : ajout de `findByEmailIgnoreCase`.
- `application.properties` : ajout de la ligne `issuer-uri`. `pom.xml` : ajout du starter resource-server.
- Front : dashboards Hôpital et Admin branchés sur l'API ; pages login/inscription redirigent vers Keycloak.
- `POST /api/utilisateurs/login` (ancien login) est conservé mais inaccessible : tout `/api/**` exige un JWT.
  À supprimer quand l'équipe est d'accord.

## Attention
Le mot de passe de la base Supabase est écrit en clair dans `application.properties` d'un repo public :
pensez à le changer dans Supabase et à le sortir du code (variables d'environnement).
