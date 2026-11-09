# Module de recommandations
## Recommandations (Q4)

Permet de recommander des produits par prédiction de la quantité vendue.

### Périmètre : ce que voit le commerçant (endpoint + paramètre)
Le commerçant lit une liste 5 produits classés et l'estimation de leurs quantité pour 1 semaine 
GET /api/recommendations?limit=5

### Données utilisées (tables / colonnes)
TABLE Ventes
id, produit, quantité, date de vente, prix

### Ce qui n'est pas du ML
système de comptages des ventes faites sans prédiction.

### Algorithme choisi et pourquoi (critères)
Modèle de Régression Linéaire parce que la cible est un nombre et le modèle est interprétable.

### Échantillon, cible, features
échantillon : un produit pour 1 semaine
feature : quantité de la semaine en cours, prix, numéro de la semaine
cible : quantité vendu semaine suivante

### Entraînement
Pendant l'entraînement, on donne au modèle toutes les lignes connues (une ligne = un produit pour une semaine), avec leurs features x et leur cible y. 
Le modèle ajuste les poids w jusqu'à ce que la somme des écarts au carré entre la quantité réelle (y) et la quantité prédites (ŷ) soit minimale. 
Ces poids, un jeu par produit, servent ensuite à la prédiction.


### Prédiction / recommandation (des ŷ à la liste de 5 produits)
Pour chaque produit, on calcule ŷ avec ses poids et les features de la dernière semaine connue. 
On tie les produits par ŷ décroissant, et on renvoie les 5 premiers.

On ajuste les poids w jsq à ce que cette somme des écarts soit minimale, avant de prédire une valeur avec ces poids afin 

### Évaluation (MAE : que veut dire « MAE = 3 » ?)
MAE = moyenne des écarts absolus entre réel et prédit

### Limites (≥ 2)
Les limites sont en cas de :
- produits nouveau : pas d'historique, ou cas de produit avec une remise.
- saisons : la droite ne tient pas compte des périodes et des saisons. Ex : une paire de tongue ne se vendra pas bien en hiver.
- peux de volume de données : peux de de semaines de vente par produit, donc produit fragile.

### Pistes d'amélioration (≥ 3)

Pour réduire ces limites on peut :
- ajouter de nouvelles features : mois, périodes de l'année.
- réentrainer modèle régulièrement.
- Rendre algorythme interchangeable.