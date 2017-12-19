# API-LeBonSandwich

## A propos

Ce projet regroupe Les TD 1 à 4 du cours de programmation Web/Server de la LP CISIIE de l'année 2017-2018.

## Comment faire fonctionner le projet

Pour faire fonctionner le projet, il faut d'abord cloner le projet :

```bash
git clone git@github.com:demarbre1u/API-LeBonSandwich.git
```

Une fois le projet cloné, on se déplace dans le repertoire, puis on construit notre fichier `.war` :

```bash
cd API-LeBonSandwich/
mvn clean install
```

Une fois notre fichier généré, il faut le déplacer dans le bon dossier de sorte que WildFly puisse le déployer.

Une fois ceci fait, il ne reste plus qu'à tester !

## Les fonctionnalités disponibles

### Les catégories

__Liste des catégories__ : localhost:8080/projet/api/categories (GET)

__Voir une catégorie__ : localhost:8080/projet/api/categories/1 (GET)

__Ajouter une catégorie__ : localhost:8080/projet/api/categories (POST)

__Modifier une catégorie__ : localhost:8080/projet/api/categories/1 (PUT)

__Supprimer une catégorie__ : localhost:8080/projet/api/categories/1 (DELETE)

__Liste des sandwichs d'une catégorie__ : localhost:8080/projet/api/categories/1/sandwichs (GET)

### Les sandwichs

__Liste des sandwichs__ : localhost:8080/projet/api/sandwichs (GET)

__Voir un sandwich__ : localhost:8080/projet/api/sandwichs/1 (GET)

__Ajouter un sandwich__ : localhost:8080/projet/api/sandwichs (POST)

__Modifier un sandwich__ : localhost:8080/projet/api/sandwichs/1 (PUT)

__Supprimer un sandwich__ : localhost:8080/projet/api/sandwichs/1 (DELETE)

__Filtrer par type de pain__ : localhost:8080/projet/api/sandwichs?type=baguette (GET)

__Filtrer par image__ : localhost:8080/projet/api/sandwichs?img=1 (GET)

__Filtrer par type de pain ET image__ : localhost:8080/projet/api/sandwichs?type=baguette&img=1 (GET)

__Changer de page__ : localhost:8080/projet/api/sandwichs?page=2 (GET)

__Changer de taille__ : localhost:8080/projet/api/sandwichs&size=50 (GET)

__Changer de taille ET de page__ : localhost:8080/projet/api/sandwichs?page=2&size=50 (GET)

__Liste des catégories d'un sandwich__ : localhost:8080/projet/api/sandwichs/1/categories (GET)

### Les commandes

__Créer une commande__ : localhost:8080/projet/api/commandes/ (POST)

__Consulter une commande__ : localhost:8080/projet/api/commandes/id?token=token __OU__ localhost:8080/projet/api/commandes/id avec X-lbs-token=token dans le Header de la requête (GET)
