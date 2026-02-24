# Guide de Déploiement - EgoraIMC Plugin

## Checklist de Déploiement

- [ ] Java 21 installé (compilation)
- [ ] Maven 3.6+ installé
- [ ] Serveur Spigot/Paper 1.21.1+ préparé
- [ ] Dossier `plugins/` accessible en écriture
- [ ] Espace disque disponible (~1 MB minimum)

## Étapes de Déploiement

### 1. Préparation de l'environnement

```bash
# Vérifier Java
java -version
# Devrait afficher: Java 21+ 

# Vérifier Maven
mvn -version
# Devrait afficher: Apache Maven 3.6+
```

### 2. Compiler le plugin

```bash
# Se placer dans le répertoire du projet
cd chemin/vers/egoriamc-bukkit

# Compiler et packager
mvn clean package
```

Résultat attendu :
```
[INFO] BUILD SUCCESS
```

Le fichier JAR se trouvera dans : `target/egoriamc-plugin-1.0-SNAPSHOT.jar`

### 3. Copier le JAR sur le serveur

#### Option A : Sur la même machine
```bash
copy target\egoriamc-plugin-1.0-SNAPSHOT.jar <serveur>\plugins\
```

#### Option B : Sur une autre machine (SCP)
```bash
scp target/egoriamc-plugin-1.0-SNAPSHOT.jar user@host:/path/to/server/plugins/
```

### 4. Redémarrer le serveur

```bash
# Via console du serveur
stop
# Attendre l'arrêt complète ~ 30 secondes
start
```

### 5. Vérifier l'installation

Regardez dans les logs du serveur :
```
[15:08:10 INFO] [EgoraIMC] Plugin activé avec succès !
[15:08:10 INFO] [EgoraIMC] - Gestion des homes : ACTIVÉE
[15:08:10 INFO] [EgoraIMC] - Gestion des messages : ACTIVÉE
```

Vérifiez les fichiers créés :
```
plugins/
  EgoraIMC/
    ├── config.yml
    ├── messages.yml
    └── homes.yml
```

## Configuration Post-Installation

### Étape 1 : Vérifier config.yml

```bash
# Les paramètres par défaut devraient suffire :
# - max-homes: 3
# - storage: file
# - use-emojis: true
```

### Étape 2 : Personnaliser messages.yml

Ouvrez `plugins/EgoraIMC/messages.yml` et personnalisez :
- Messages de join
- Messages de leave
- Messages de mort
- Messages des homes

### Étape 3 : Appliquer la configuration

**Option A : Redémarrer** (recommandé)
```bash
stop
start
```

**Option B : Rechargement sans redémarrage** (à développer)
```bash
/egoriamc reload  # Commande à ajouter
```

## Tests Post-Déploiement

### Test 1 : Vérifier le plugin
```bash
/plugins
# Devrait afficher : EgoraIMC, EgoraIMC v1.0
```

### Test 2 : Créer un home
```bash
/home set maison
# Devrait afficher : "Votre home maison a été créé avec succès !"
```

### Test 3 : Lister les homes
```bash
/home list
# Devrait afficher : "=== Vos homes ===" suivi de la liste
```

### Test 4 : Téléporter à un home
```bash
/home tp maison
# Devrait vous téléporter au home créé
```

### Test 5 : Vérifier les messages
Connectez/déconnectez des joueurs et vérifiez que les messages s'affichent.

## Dépannage du Déploiement

### Erreur : "Plugin not found"
- Vérifier que le JAR est bien dans `plugins/`
- Vérifier le nom du fichier
- Vérifier les permissions du dossier

### Erreur : "Unsupported class version"
- Java 17+ requis pour l'exécution
- Vérifier avec : `java -version`
- Mettre à jour Java si nécessaire

### Plugin chargé mais pas actif
- Vérifier les logs du serveur pour les erreurs
- Vérifier que `plugin.yml` est valide
- Vérifier les erreurs de compilation (pom.xml)

### Commandes ne fonctionnent pas
- Vérifier que les permissions sont possédées
- Vérifier le nom exact de la commande : `/home`
- Vérifier les logs pour les erreurs

### Les homes ne se sauvegardent pas
- Vérifier l'espace disque
- Vérifier les permissions du dossier `plugins/EgoraIMC/`
- Vérifier les logs du serveur

## Backup des Données

### Avant une mise à jour

```bash
# Sauvegarder les homes
copy plugins\EgoraIMC\homes.yml plugins\EgoraIMC\homes.backup.yml

# Sauvegarder la config
copy plugins\EgoraIMC\config.yml plugins\EgoraIMC\config.backup.yml
copy plugins\EgoraIMC\messages.yml plugins\EgoraIMC\messages.backup.yml
```

### Restaurer après erreur

```bash
# Restaurer les homes
copy plugins\EgoraIMC\homes.backup.yml plugins\EgoraIMC\homes.yml

# Restaurer la config
copy plugins\EgoraIMC\config.backup.yml plugins\EgoraIMC\config.yml
copy plugins\EgoraIMC\messages.backup.yml plugins\EgoraIMC\messages.yml
```

## Mise à Jour du Plugin

### Procédure

1. **Arrêter le serveur**
   ```bash
   stop
   ```

2. **Sauvegarder les données** (voir section Backup)

3. **Remplacer le JAR**
   ```bash
   copy target\egoriamc-plugin-1.0-SNAPSHOT.jar <serveur>\plugins\
   ```

4. **Redémarrer le serveur**
   ```bash
   start
   ```

5. **Vérifier les logs**
   ```
   [EgoraIMC] Plugin activé avec succès !
   ```

## Désinstallation

### Procédure

1. **Arrêter le serveur**

2. **Supprimer le JAR**
   ```bash
   del plugins\egoriamc-plugin-1.0-SNAPSHOT.jar
   ```

3. **Optionnel : Supprimer les données**
   ```bash
   rmdir plugins\EgoraIMC  # Windows
   rm -rf plugins/EgoraIMC  # Linux/Mac
   ```

4. **Redémarrer le serveur**

## Vérification de la Santé du Plugin

### Commandes de diagnostic

```bash
# Vérifier que le plugin est chargé
/plugins

# Vérifier les permissions
/perms check <joueur> egoriamc.home.use

# Consulter les logs
tail -f logs/latest.log | grep EgoraIMC
```

### Indicateurs de bonne santé

- Aucune erreur dans les logs
- Commandes `/home` fonctionnelle
- Fichiers config présents et lisibles
- Emoji visibles en console (si configuré)

## Support et Assistance

Voir : [README.md](README.md) section "Support"
