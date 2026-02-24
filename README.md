# EgoraIMC - Plugin Bukkit 1.21.1

Un plugin Bukkit complet pour la gestion des homes des joueurs et des messages personnalisables de bienvenue, départ et mort.

## Fonctionnalités

### 🏠 Gestion des Homes
- **Créer des homes** : `/home set <nom>`
- **Téléportation** : `/home tp <nom>` ou `/home` pour le premier
- **Lister les homes** : `/home list`
- **Supprimer les homes** : `/home delete <nom>`
- **Nombre maximum configurable** par joueur (défaut: 3)
- **Sauvegarde automatique** en fichier YAML

### 📢 Gestion des Messages
- **Messages de join** : Personnalisable avec emojis du resource pack
- **Messages de leave** : Notification de départ avec emoji
- **Messages de mort** : Affichage de la cause avec détails (tueur, etc.)
- **Tous les messages sont configurables** via `messages.yml`

### 🎨 Intégration des Emojis
- **Support des emojis du resource pack** personnalisé
- 6 types d'emojis disponibles : Owner, Admin, Developer, Staff, VIP, Member
- **Logs du console avec emojis** pour une meilleure lisibilité
- Compatible avec les textures personnalisées Minecraft

## Installation

### Prérequis
- Java 21+ (compilation) / Java 17+ (exécution)
- Maven 3.6+
- Serveur Spigot/Paper 1.21.1+

### Étapes
1. **Compiler le plugin** :
   ```bash
   mvn clean package
   ```

2. **Placer le JAR** dans le dossier `plugins/` de votre serveur
   ```bash
   cp target/egoriamc-plugin-1.0-SNAPSHOT.jar <serveur>/plugins/
   ```

3. **Redémarrer le serveur**

4. **Configurer** (optionnel) :
   - Éditer `plugins/EgoraIMC/config.yml`
   - Éditer `plugins/EgoraIMC/messages.yml`

## Commandes

| Commande | Description | Permission |
|----------|-------------|-----------|
| `/home` | Téléporter au premier home | `egoriamc.home.use` |
| `/home set <nom>` | Créer un home | `egoriamc.home.use` |
| `/home tp <nom>` | Téléporter à un home | `egoriamc.home.use` |
| `/home delete <nom>` | Supprimer un home | `egoriamc.home.use` |
| `/home list` | Lister les homes | `egoriamc.home.use` |

## Configuration

### config.yml
```yaml
homes:
  max-homes: 3        # Nombre max de homes par joueur
  storage: file       # Stockage (file ou database)

logging:
  use-emojis: true    # Utiliser les emojis du resource pack
  default-emoji: member  # Emoji par défaut
```

### messages.yml
```yaml
homes:
  set-success: "&aVotre home &e{name}&a a été créé !"
  delete-success: "&aVotre home &e{name}&a a été supprimé !"
  tp-success: "&aTéléportation vers le home &e{name}&a..."
  not-found: "&cCe home n'existe pas."
  limit-reached: "&cVous avez atteint le nombre maximum (&e{max}&c)."
  
join:
  message: "&6{player} &erejoint le serveur"
  emoji: "member"

leave:
  message: "&6{player} &ea quitté le serveur"
  emoji: "member"

death:
  message: "&c{player} &eest mort &7({cause})"
  emoji: "developer"
```

## Architecture

```
egoriamc-bukkit/
├── src/main/java/me/egoriamc/
│   ├── EgoraIMC.java              # Classe principale
│   ├── util/EmojiUtil.java        # Gestion des emojis
│   ├── manager/
│   │   ├── ConfigManager.java     # Fichiers config
│   │   ├── MessageManager.java    # Messages
│   │   └── HomeManager.java       # Gestion homes
│   ├── command/HomeCommand.java   # Commande /home
│   └── listener/PlayerEventListener.java  # Événements
└── src/main/resources/
    ├── plugin.yml
    ├── config.yml
    └── messages.yml
```

## Emojis Disponibles

| Emoji | Constante | Usage |
|-------|-----------|--------|
| ϕ | OWNER | Propriétaire du serveur |
| ϖ | ADMIN | Administrateur |
| Ͱ | DEVELOPER | Développeur |
| ᾞ | STAFF | Staff |
| Ͳ | VIP | Joueur VIP |
| ϼ | MEMBER | Joueur normal |

### Exemple d'utilisation dans les messages
```yaml
join:
  message: "&6{player} &erejoint"
  emoji: "owner"  # Utilisera l'emoji ϕ
```

## Permissions

| Permission | Description | Défaut |
|-----------|-------------|--------|
| `egoriamc.home.use` | Utiliser les homes | `true` |
| `egoriamc.home.admin` | Admin homes | `op` |

## Stockage des Données

### homes.yml
Généré automatiquement, stocke les emplacements des homes :
```yaml
550e8400-e29b-41d4-a716-446655440000:
  maison:
    location: world,-100.5,65,200.5,0,0
```

### Format des locations
Le plugin utilise le format standard Bukkit pour les locations (monde, x, y, z, yaw, pitch).

## Dépannage

### ❌ Le plugin ne démarre pas
- Vérifier Java 21+ : `java -version`
- Vérifier Spigot 1.21.1+
- Consulter les logs du serveur

### ❌ Les homes ne se sauvegardent pas
- Vérifier les permissions du dossier `plugins/EgoraIMC/`
- Vérifier l'espace disque
- Vérifier les logs d'erreur

### ❌ Les emojis ne s'affichent pas
- Assurez-vous que le resource pack est activé côté client
- Vérifier que `use-emojis: true` dans config.yml
- Le resource pack doit avoir les fichiers `fonts/owner.png`, `fonts/admin.png`, etc.

## Développement

### Ajouter une nouvel commande
1. Créer une classe implémentant `CommandExecutor`
2. L'enregistrer dans `EgoraIMC.onEnable()`
3. Ajouter l'entrée dans `plugin.yml`

### Ajouter un nouvel événement
1. Créer une méthode avec `@EventHandler` dans `PlayerEventListener`
2. Utiliser `MessageManager` pour les messages

## Version

- **Version du plugin** : 1.0
- **Version Bukkit requise** : 1.21.1+
- **Java requis** : 17+ (compilé pour Java 21)

## Licence

Voir le fichier [LICENSE](LICENSE)

## Support

Pour les problèmes ou suggestions, créez une issue sur le dépôt.
