# Architecture - EgoriaMC Plugin

## Structure du projet

```
egoriamc-bukkit/
├── src/main/
│   ├── java/me/egoriamc/
│   │   ├── EgoriaMC.java                 # Classe principale du plugin
│   │   ├── util/
│   │   │   └── EmojiUtil.java            # Utilitaire pour les emojis
│   │   ├── manager/
│   │   │   ├── ConfigManager.java        # Gestion des fichiers config
│   │   │   ├── MessageManager.java       # Gestion des messages
│   │   │   └── HomeManager.java          # Gestion des homes
│   │   ├── command/
│   │   │   └── HomeCommand.java          # Commande /home
│   │   └── listener/
│   │       └── PlayerEventListener.java  # Événements joueur
│   └── resources/
│       ├── plugin.yml                    # Configuration du plugin
│       ├── config.yml                    # Configuration générale
│       └── messages.yml                  # Messages personnalisables
└── pom.xml                                # Configuration Maven
```

## Description des modules

### EgoriaMC.java

Point d'entrée du plugin. Gère:

- L'initialisation des gestionnaires
- L'enregistrement des commandes et événements
- Les logs avec les emojis du resource pack

### Managers

#### ConfigManager

Lit et stocke les configurations:

- `config.yml` - Paramètres globaux
- `messages.yml` - Messages personnalisables

#### MessageManager

Gère les messages:

- Traduction des codes de couleur (`&` → `§`)
- Remplacement des placeholders
- Insertion des emojis

#### HomeManager

Gère les homes des joueurs:

- Création/suppression de homes
- Cache en mémoire pour performance
- Sauvegarde en fichier YAML (`homes.yml`)

### Commands

#### HomeCommand

Implémente la commande `/home`:

- `/home` - Téléport au premier home
- `/home set <nom>` - Créer un home
- `/home tp <nom>` - Téléporter à un home
- `/home delete <nom>` - Supprimer un home
- `/home list` - Lister les homes

### Listeners

#### PlayerEventListener

Écoute les événements:

- `PlayerJoinEvent` - Affiche message de join
- `PlayerQuitEvent` - Affiche message de leave
- `PlayerDeathEvent` - Affiche message de mort avec cause

### Utils

#### EmojiUtil

Fournis les emojis du resource pack:

- Constantes : OWNER, ADMIN, DEVELOPER, STAFF, VIP, MEMBER
- Méthode `getEmoji()` pour récupérer un emoji
- Méthode `formatWithEmoji()` pour formater un message

## Flux de données

### Démarrage du plugin

```
onEnable()
  ├─ ConfigManager.reload() -> charge config.yml & messages.yml
  ├─ MessageManager.init()
  ├─ HomeManager.reload() -> charge homes.yml et cache
  ├─ Enregistrement de HomeCommand
  └─ Enregistrement de PlayerEventListener
```

### Utilisation d'un home

```
/home set myHome
  ├─ HomeCommand.handleSet()
  ├─ HomeManager.createHome()
  ├─ Vérification de la limite (config.yml)
  ├─ Sauvegarde dans homes.yml
  └─ Message utilisateur (messages.yml)
```

### Événement joueur

```
Player joins
  ├─ PlayerEventListener.onPlayerJoin()
  ├─ MessageManager.getJoinMessage()
  ├─ EmojiUtil.formatWithEmoji() -> ajoute emoji
  ├─ Affichage en chat
  └─ Log console
```

## Stockage des données

### config.yml

Paramètres globaux:

- Nombre max de homes par joueur
- Type de stockage
- Paramètres de logging

### messages.yml

Messages personnalisables:

- Messages pour les homes
- Messages pour join/leave/death
- Support des placeholders

### homes.yml

Données des homes (généré automatiquement):

```yaml
<uuid-joueur>:
  <nom-home>:
    location: <Location Bukkit>
```

## Points d'extensibilité

### Ajouter une nouvelle commande

1. Créer une classe `implements CommandExecutor`
2. L'enregistrer dans `EgoriaMC.onEnable()`
3. Ajouter l'entrée dans `plugin.yml`

### Ajouter un nouvel événement

1. Créer une méthode avec `@EventHandler`
2. L'ajouter à `PlayerEventListener`
3. Utiliser `MessageManager` pour les messages

### Personnaliser les messages

1. Modifier le fichier `messages.yml`
2. Relancer le serveur OU utiliser une commande de reload (à développer)

### Utiliser les emojis

1. Importer `EmojiUtil`
2. Utiliser `EmojiUtil.getEmoji(type)` ou `EmojiUtil.formatWithEmoji(type, message)`
