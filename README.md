# API - Le Bon Sandwich

## Auteur

Allan DEMARBRE

Nacera ELIAS

## A propos

Ce projet regroupe les TD 1 à 7 du cours de programmation Web/Server de la LP CISIIE de l'année 2017-2018, et plus.

L'API mise à disposition dans ce projet est CORS compliant.

## Comment faire fonctionner le projet

Pour faire fonctionner le projet, il faut d'abord cloner le projet :

```bash
git clone git@github.com:demarbre1u/api-le-bon-sandwich.git
```

Une fois le projet cloné, on se déplace dans le repertoire, puis on construit notre fichier `.war` :

```bash
cd api-le-bon-sandwich/
mvn clean install
```

Une fois notre fichier généré, il faut le déplacer dans le bon dossier de sorte que WildFly puisse le déployer.

Une fois ceci fait, il ne reste plus qu'à tester !

## Les fonctionnalités publiques disponibles

### Les catégories

#### GET 

 - Liste des catégories :

   > localhost:8080/projet/api/categories

 - Voir une catégorie : 

   > localhost:8080/projet/api/categories/1

 - Liste des sandwichs d'une catégorie : 

   > localhost:8080/projet/api/categories/1/sandwichs

#### POST 

 - Ajouter une catégorie : 

   > localhost:8080/projet/api/categories

#### PUT

 - Modifier une catégorie :

   > localhost:8080/projet/api/categories/1

#### DELETE 

 - Supprimer une catégorie : 

   > localhost:8080/projet/api/categories/1

### Les sandwichs

#### GET

 - Liste des sandwichs : 

   > localhost:8080/projet/api/sandwichs

 - Voir un sandwich : 

   > localhost:8080/projet/api/sandwichs/1

 - Filtrer par type de pain : 

   > localhost:8080/projet/api/sandwichs?type=baguette

 - Filtrer par image : 

   > localhost:8080/projet/api/sandwichs?img=1

 - Filtrer par type de pain ET image : 

   > localhost:8080/projet/api/sandwichs?type=baguette&img=1

 - Changer de page : 

   > localhost:8080/projet/api/sandwichs?page=2

 - Changer de taille : 

   > localhost:8080/projet/api/sandwichs&size=50

 - Changer de taille ET de page : 

   > localhost:8080/projet/api/sandwichs?page=2&size=50

 - Liste des catégories d'un sandwich : 

   > localhost:8080/projet/api/sandwichs/1/categories

 - Liste des tailles d'un sandwich : 

   > localhost:8080/projet/api/sandwichs/1/tailles

#### POST

 - Ajouter un sandwich : 

   > localhost:8080/projet/api/sandwichs

#### PUT

 - Modifier un sandwich : 

   > localhost:8080/projet/api/sandwichs/1 

#### DELETE

 - Supprimer un sandwich : 

   > localhost:8080/projet/api/sandwichs/1

### Les tailles

#### GET

 - Consulter la liste des tailles : 

   > localhost:8080/projet/api/tailles

 - Consulter une taille en particulier : 

   > localhost:8080/projet/api/tailles/1

 - Consulter la liste des sandwichs d'une taille : 

   > localhost:8080/projet/api/tailles/1/sandwichs

#### POST

 - Ajouter une taille : 

   > localhost:8080/projet/api/tailles

#### PUT

 - Modifier une taille : 

   > localhost:8080/projet/api/tailles/1

#### DELETE

 - Supprimer une taille : 

   > localhost:8080/projet/api/tailles/1

### Les commandes

#### GET

 - Consulter une commande : 

   > localhost:8080/projet/api/commandes/id?token=token 
   > 
   > localhost:8080/projet/api/commandes/id avec __X-lbs-token=[token]__ dans le Header de la requête (GET)

#### POST

 - Créer une commande : 

   > localhost:8080/projet/api/commandes/

 - Créer une commande fidélisée : 

   > localhost:8080/projet/api/commandes?card=uidCarte avec __Authorization : Bearer [token]__ dans le header

 - Payer une commande (non payée) : 

   > localhost:8080/projet/api/commandes/uidCommande (POST) avec __numCarte = [numCarte]__ (16 chiffres) et __dateExpiration = [dateExpiration]__ (date au format 01/18) dans le Header

 - Ajouter un sandwich à une commande : 

   > localhost:8080/projet/api/commandes/uidCommande/sandwich?uidSandwich=1&uidTaille=1&nbSandwich=2

#### PUT

 - Modifier la date de livraison d'une commande : 

   > localhost:8080/projet/api/commandes/uidCommande avec __date = [date]__ et __heure = [heure]__ dans le Header

### Les utilisateurs

#### POST

 - Créer un utilisateur : 

   > localhost:8080/projet/api/users (POST) 

### Les cartes

#### GET

 - Consulter une carte : 

   > localhost:8080/projet/api/cartes/1 avec le token récupérer lors de l'authentifiation dans le champs __Authorization : Bearer [token]__

#### POST

 - Créer une nouvelle carte : 

   > localhost:8080/projet/api/cartes

 - S'authentifier pour récupérer le token d'une carte : 

   > localhost:8080/projet/api/cartes/1/auth avec __Auth Basic__

## Les fonctionnalités privées disponibles

### Les commandes

#### GET

 - Liste des commandes : 

   > localhost:8080/projet/api/commandes/private

 - Détails d'une commande : 

   > local:8080/projet/api/commandes/uidCommande/private/

#### PUT

 - Changer le status d'une commande : 

   > local:8080/projet/api/commandes/uidCommande/private
