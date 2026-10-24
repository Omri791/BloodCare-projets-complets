# BloodCare — Angular template

Template Angular pour une plateforme de gestion des dons de sang avec 4 rôles :
- Donneur
- Centre de don
- Hôpital
- Administrateur

## Architecture

`core/` : layout, services globaux, guard  
`shared/` : modèles et composants réutilisables  
`features/` : fonctionnalités métier et dashboards par acteur

## Lancer

```bash
npm install
ng serve
```

Puis ouvrir `http://localhost:4200`.

## Spring Boot

Le template est préparé pour une API Spring Boot sur :

`http://localhost:8080/api`

Le fichier `src/app/core/services/api.service.ts` centralise les appels HTTP.

Les données affichées dans les dashboards/articles/dons sont actuellement des données de démonstration. La prochaine étape consiste à remplacer ces données par les endpoints Spring Boot.

## Rôles

La connexion de démonstration permet de choisir le rôle. Elle utilise localStorage pour simuler une session. Pour la production, remplacez-la par JWT + API Spring Security.
