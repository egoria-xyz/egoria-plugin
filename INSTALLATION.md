# Guide d'installation - EgoraIMC Plugin

## Prérequis
- Java 17+
- Maven 3.6+
- Serveur Spigot/Paper 1.21.1+

## Compilation

### Avec Maven
```bash
mvn clean package
```

Le fichier JAR sera généré dans `target/egoriamc-plugin-1.0-SNAPSHOT.jar`

## Installation

1. **Placer le JAR** dans le dossier `plugins/` de votre serveur Bukkit
2. **Redémarrer** le serveur
3. **Configurer** les fichiers:
   - `plugins/EgoraIMC/config.yml` - Configuration générale
   - `plugins/EgoraIMC/messages.yml` - Messages personnalisables

## Configuration

### config.yml
```yaml
homes:
  max-homes: 3  # Nombre maximum de homes par joueur
  storage: file # Type de stockage (file ou database)

logging:
  use-emojis: true      # Utiliser les emojis du resource pack
  default-emoji: member # Emoji par défaut
```

### messages.yml
```yaml
homes:
  set-success: "&aVotre home &e{name}&a a été créé !"
  delete-success: "&aVotre home &e{name}&a a été supprimé !"
  # ... autres messages
```

## Commandes

### /home
- `/home` - Téléporter au premier home
- `/home set <nom>` - Créer un home
- `/home tp <nom>` - Téléporter à un home
- `/home delete <nom>` - Supprimer un home
- `/home list` - Lister vos homes

## Permissions

- `egoriamc.home.use` - Permission d'utiliser les homes (défaut: true)
- `egoriamc.home.admin` - Permission d'administrer les homes (défaut: op)

## Emojis disponibles

Le plugin supporte les emojis du resource pack personnalisé:
- `owner` (ϕ)
- `admin` (ϖ)
- `developer` (Ͱ)
- `staff` (ᾞ)
- `vip` (Ͳ)
- `member` (ϼ)

Les emojis s'affichent automatiquement dans:
- Les messages de join/leave/death
- Les logs du console

## Structure des fichiers

```
plugins/EgoraIMC/
├── config.yml      # Configuration générale
├── messages.yml    # Messages personnalisables
└── homes.yml       # Données des homes (généré automatiquement)
```

## Dépannage

### Le plugin ne démarre pas
- Vérifier la version de Java (minimum 17)
- Vérifier la version de Spigot (minimum 1.21.1)
- Consulter la console pour les erreurs

### Les homes ne se sauvegardent pas
- Vérifier que le dossier `plugins/EgoraIMC/` n'est pas en lecture seule
- Vérifier l'espace disque disponible

### Les emojis ne s'affichent pas
- Assurez-vous que le resource pack personnalisé est activé côté client
- Vérifier que `logging.use-emojis` est à `true` dans config.yml
