## Q1 - DIAGRAMME DE COMPOSANTS UML - solution refonte

### ANALYSE BESOIN
La typologie de clients est peut explicite, par contre les objets clé (ventes, catalogue de produits, gestion des rdv)
où chaque service est un ensemble de fonctionnalités et devraient isolés dans des modules séparés pour assurer de la modularité à termes.

Comment s'assurer que la corruption d'un client ne viennent pas alterer l'intégralité des données des autres clients ?
Comment offrir de l'évolutivité pour chaque module sans avoir à reprendre l'intégralité du logiciel ?
Comment assurer de la scalabilité pour cette application ?

L'Architecture par services répond aux besoins d'évolutivité, de robustesse et de maintenance sur le long termes pour plusieurs raisons.

- Facile d'accès externe, controle interne garantie : à termes peut être que les clients se connecteront via API à la solution.
- Multi-technologie : éviter la duplication de code à termes, plusieurs app se connecteront au même service.
- Plus de sécurité : Maitrise des données offertes, ce qui facilite

### LISTE MODULES - DEPENDANCES

- Composants
Catalogue -> contient les classes métiers : produit couches : dto, controller, service, repository.
Ventes -> classes métiers : date_heure, stock, promotion. couches : dto, controller, service, repository.
_Ventes -- ( --Catalogue_

RendezVous -> classes métiers: rdv, points, date_heure. couches : dto, controller, service, repository.
Boutiques -> classe métiers : commerçant, localité. couches : dto, controller, service, repository.
_Boutiques -- ) -- RendezVous_

ML Recommandations -> algorythme machine Learing Recommadnations.
_ML Recommndations --( Ventes_

DB Maria Boutiques / RendezVous / Ventes -> stocks toutes les données avec table associée.

DB Maria Catalogue

DB No-SQL -> stocker ML
Angular Frontend -> Espace client, espace commerçant

## Q2 - CONCEPTION MODULE DE VENTES

- Schéma base de données :

TABLE Ventes
ID id
FOREIN KEY Ligne_vente_ID
FOREIGN KEY Boutique_ID
LOCAL_DATE date_achat
FOREIGN KEY discount
INT total
FLOAT prix

TABLE Ligne_vente
INT quantité_produit
INT prix

TABLE discount
ID id
FLOAT reduce


## Q3 - Amélioration de la qualité du code

Comment s'assurer que les données sensibles sont bien accessibles seulement pour les user pointés ?
Quels sont les users ciblés ?


### 

Classe Utilisateur
Extends Client, Commerçant

Commerçant accès admin Ventes, Boutique.

| code HTTP | comportement |
| --------- | ------------ |
| 403 | accès non autorisés |
| 401 | token invalide |
| 403 | action a échoué : message explicite. |

## Q5 - Stratégies de tests
Pourquoi certains tests ?
Plusieurs nv de tests, tests unitaires, test d'intégrations et tests de dépploiement.

## 1° niveau : TDD & BDD
Coder résultat attendu pour les fonctionnalités du module de vente avant de coder les méthodes.
Vérifier que les types sont bien déclarés et respectés dans les méthodes.

#### TDD
    - vérifier qu'une vente peut contenir plusieurs produits & que le total est calculé automatiquement.
    - trie des ventes par date
##### TDD - sécurité authentification
    - tentative non auth -> HTTP 403
    - req ss token -> 401
##### BDD - authentification
    - codes HTTP sont cohérents (400, 401, 403, 404).
    - erreurs suivent un format JSON standard

##### TDD - journalisation
    - erreurs suivent un format JSON standard

## 2° niveau : test d'intégration
Workflow github qui permet de vérifier que les tests unitaires s'exec bien sur un runner github distants via le .gitlab-ci.yml qui gère l'execution des tests dans le runner.

## 3° niveau : test fonctionnels
Vérifier que les fonctionnalités répondent bien aux US de l'énoncé.
tester le mécanisme de sécurité, authentification et login avec SpringSecurity et la génération de Bearer Token.

## test d'API ML
    - module doit être ds Archi modulaire
    - recommandations sont calculés à partir des ventes & du catalogue produit.
    - module puisse évoluer.





