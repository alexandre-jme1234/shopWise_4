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





