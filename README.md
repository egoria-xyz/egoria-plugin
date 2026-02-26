# EgoriaMC - Plugin Bukkit 1.21.1

Un plugin Bukkit complet pour la gestion complète d'un serveur SMP/Survival avec homes, warps, backpack, système économique et bien plus.

## 🎮 Fonctionnalités principales

### 🏠 Gestion des Homes

- Créer/supprimer/lister/téléporter aux homes
- Nombre de homes configurable par joueur (défaut: 3)
- Visualisation GUI des homes
- Sauvegarde automatique en fichier/base de données

### 🗺️ Gestion des Warps

- Créer/supprimer/lister/téléporter aux warps
- Warps globaux accessibles à tous les joueurs
- Affichage des infos du warp (créateur, localisation)
- Recherche rapide par nom

### 🎒 Système de Backpack personnalisé

- 1 ligne (9 slots) par défaut, 2 lignes pour les Streamers (18 slots)
- Déverrouillage de slots avec argent (Vault)
- Prix exponentiel : 100$ → 150$ → 225$ → ...
- Sauvegarde automatique toutes les 30 secondes
- **Protection** : items sauvegardés à la mort

### 🌙 Vote2Sleep - Passer la nuit ensemble

- Vote démocratique pour passer la nuit
- Besoin de 50% des joueurs pour valider
- Cooldown de 1 minute entre chaque vote
- Notifications sonores et messages

### 💬 Système de Messages avancés

- **Messages de join** : Personnalisables avec emojis
- **Messages de leave** : Notification de départ
- **Messages de mort** : Affiches causes (combat, noyade, etc.)
- **Mentions** : Système @pseudo avec notifications
- **Annonces globales** : `/annonce` avec séparateurs et sons

### ⚠️ Système de Warnings (Avertissements)

- Avertissements enregistrés en base de données
- Historique complet par joueur
- Commande `/warn <pseudo> <raison>`

### 🎁 Commandes bonus

- **Furnace** (`/furnace`) : Cuisson automatique pour Gardiens
- **Craft** (`/craft`) : Table de craft portative pour Gardiens
- **Live** (`/live <URL>`) : Annonce de live pour Streamers
- **Plugins** (`/plugins`) : Liste GUI paginée des plugins
- **Reload** (`/reload`) : Recharge la config du plugin

### 🎨 Intégration des Emojis

- Support emojis du resource pack personnalisé
- 6 types disponibles : Owner, Admin, Developer, Staff, VIP, Member
- Logs console avec emojis

## 📋 Liste complète des commandes

| Commande                  | Description                 | Permission              |
| ------------------------- | --------------------------- | ----------------------- |
| `/home`                   | Gérer les homes             | `egoriamc.home.use`     |
| `/home set <nom>`         | Créer un home               | `egoriamc.home.use`     |
| `/home tp <nom>`          | Téléporter à un home        | `egoriamc.home.use`     |
| `/home delete <nom>`      | Supprimer un home           | `egoriamc.home.use`     |
| `/home list`              | Lister les homes            | `egoriamc.home.use`     |
| `/home gui`               | GUI des homes               | `egoriamc.home.use`     |
| `/warp`                   | Gérer les warps             | `egoriamc.warp.use`     |
| `/warp set <nom>`         | Créer un warp (OP)          | `egoriamc.warp.admin`   |
| `/warp delete <nom>`      | Supprimer un warp (OP)      | `egoriamc.warp.admin`   |
| `/warp <nom>`             | Téléporter à un warp        | `egoriamc.warp.use`     |
| `/backpack`               | Ouvrir le backpack          | `egoriamc.backpack.use` |
| `/vote2sleep`             | Voter pour passer la nuit   | `egoriamc.vote2sleep`   |
| `/annonce <msg>`          | Envoyer une annonce (OP)    | `egoriamc.announce`     |
| `/plugins`                | Lister les plugins (GUI)    | `egoriamc.plugins`      |
| `/furnace`                | Cuire auto (Gardien)        | `egoriamc.furnace.use`  |
| `/craft`                  | Table de craft (Gardien)    | `egoriamc.craft.use`    |
| `/live <URL>`             | Annoncer un live (Streamer) | `egoriamc.live.use`     |
| `/warn <pseudo> <raison>` | Avertir un joueur (OP)      | `egoriamc.warn`         |
| `/reload`                 | Recharger config (OP)       | `egoriamc.reload`       |
| `/help`                   | Afficher l'aide             | `egoriamc.help`         |

## ⚙️ Configuration

## ⚙️ Configuration

### 📦 Prérequis

- **Java** : 21+ (compilation) / 17+ (exécution)
- **Maven** : 3.6+
- **Serveur** : Spigot/Paper 1.21.1+
- **Dépendances** : Vault (pour économie du backpack)

### 🔧 Installation

1. **Compiler le plugin**

```bash
mvn clean package
```

2. **Copier le JAR compilé**

```bash
cp target/egoriamc-plugin-1.6.jar /chemin/vers/serveur/plugins/
```

3. **Redémarrer le serveur**

4. **Fichiers de config générés automatiquement** dans `plugins/EgoriaMC/` :
   - `config.yml` - Configuration générale
   - `messages.yml` - Messages personnalisables
   - `plugin.yml` - Inscription des commandes
   - `emojis.yml` - Mapping des emojis
   - `homes.yml` - Homes des joueurs
   - `warps.yml` - Warps du serveur
   - `backpack-inventories/` - Dossier des backpacks
   - `backpacks/` - Dossier des données de déverrouillage

### ⚙️ config.yml

```yaml
# Homes
homes:
  max-homes: 3
  storage: file

# Warps
warps:
  enable-cost: false
  cost: 0

# Backpack
backpack:
  base-price: 100
  exponential-factor: 1.5
  normal-lines: 1 # 1 ligne = 9 slots
  streamer-lines: 2 # 2 lignes = 18 slots

# Vote2Sleep
vote2sleep:
  required-percentage: 0.5 # 50% des joueurs
  cooldown: 60 # secondes

# Annonces
announce:
  prefix: "&c&l[ANNONCE]"
  format: "{prefix} &r{message}"
  sound:
    enabled: true
    type: "ENTITY_ENDER_DRAGON_GROWL"
  separator:
    enabled: true
```

### 📝 messages.yml

Tous les messages sont personnalisables. Exemples :

```yaml
homes:
  set-success: "&aVotre home &e{name}&a a été créé !"
  delete-success: "&aVotre home &e{name}&a a été supprimé !"
  tp-success: "&aTéléportation vers &e{name}&a..."
  not-found: "&cCe home n'existe pas."

warps:
  set-success: "&aLe warp &e{name}&a a été créé !"
  tp-success: "&aTéléportation vers &e{name}&a..."

backpack:
  no-permission: "&cVous n'avez pas la permission."
  slot-unlocked: "&a✓ Slot &e{0}&a déverrouillé pour &6${1}"
  not-enough-money: "&cIl vous manque &6${0}"

announce:
  sent: "&aAnnonce envoyée avec succès !"
```

## 🏗️ Architecture du projet

```
egoriamc-bukkit/
├── src/main/java/me/egoriamc/
│   ├── EgoriaMC.java                    # Classe principale
│   ├── command/
│   │   ├── HomeCommand.java             # /home
│   │   ├── WarpCommand.java             # /warp
│   │   ├── BackpackCommand.java         # /backpack
│   │   ├── Vote2SleepCommand.java       # /vote2sleep
│   │   ├── AnnounceCommand.java         # /annonce
│   │   ├── WarnCommand.java             # /warn
│   │   ├── PluginsCommand.java          # /plugins (GUI)
│   │   ├── FurnaceCommand.java          # /furnace
│   │   ├── CraftCommand.java            # /craft
│   │   ├── LiveCommand.java             # /live
│   │   ├── ReloadCommand.java           # /reload
│   │   ├── HelpCommand.java             # /help
│   │   └── PluginsCommand.java          # /plugins
│   ├── manager/
│   │   ├── ConfigManager.java           # Gestion config
│   │   ├── MessageManager.java          # Messages & couleurs
│   │   ├── HomeManager.java             # Gestion des homes
│   │   ├── WarpManager.java             # Gestion des warps
│   │   ├── BackpackManager.java         # Gestion backpack
│   │   ├── BackpackInventoryManager.java # Sérialisation items
│   │   ├── DatabaseManager.java         # Base de données
│   │   ├── WarnManager.java             # Avertissements
│   │   ├── AutoMessageManager.java      # Messages auto
│   │   └── SpawnConfigManager.java      # Config spawn
│   ├── listener/
│   │   ├── PlayerEventListener.java     # Événements joueurs
│   │   ├── ChatListener.java            # Chat (@mentions)
│   │   ├── HomeInventoryListener.java   # GUI homes
│   │   ├── PluginsInventoryListener.java # GUI plugins
│   │   ├── BackpackInventoryListener.java # GUI backpack
│   │   ├── BackpackSaveListener.java    # Auto-save backpack
│   │   ├── CreatureSpawnListener.java   # Spawn control
│   │   └── MentionListener.java         # Notifications
│   └── util/
│       └── EmojiUtil.java               # Gestion emojis
└── src/main/resources/
    ├── plugin.yml                       # Config plugin
    ├── config.yml                       # Config générale
    ├── messages.yml                     # Messages
    ├── emojis.yml                       # Emojis
    └── sql/warns.sql                    # Formation BDD
```

## 💾 Format de stockage

### Homes (YAML)

```yaml
550e8400-e29b-41d4-a716-446655440000:
  maison:
    location: world,-100.5,65,200.5,0,0
  spawn:
    location: world,0,64,0,0,0
```

### Warps (YAML)

```yaml
spawn:
  location: world,0,64,0,0,0
  creator: Owner
```

### Backpack (Slots déverrouillés - YAML)

```yaml
unlocked-slots: [0, 1, 2, 3, 4]
```

### Warnings (Base de données MySQL)

```sql
CREATE TABLE warns (
  id INT AUTO_INCREMENT PRIMARY KEY,
  player_uuid VARCHAR(36),
  player_name VARCHAR(16),
  warned_by VARCHAR(16),
  reason TEXT,
  date TIMESTAMP
);
```

## 🎨 Emojis disponibles

| Emoji | Groupe    | Constante   |
| ----- | --------- | ----------- |
| ϕ     | Owner     | `owner`     |
| ϖ     | Admin     | `admin`     |
| Ͱ     | Developer | `developer` |
| ᾞ     | Staff     | `staff`     |
| Ͳ     | VIP       | `vip`       |
| ϼ     | Member    | `member`    |

Utilisation dans messages.yml :

```yaml
join:
  message: "&6{player} &erejoint"
  emoji: "vip" # Affichera l'emoji VIP
```

## 🔐 Système de permissions

| Permission              | Description                  | Défaut |
| ----------------------- | ---------------------------- | ------ |
| `egoriamc.help`         | Voir l'aide                  | `true` |
| `egoriamc.plugins`      | Voir plugins (GUI)           | `true` |
| `egoriamc.home.use`     | Utiliser les homes           | `true` |
| `egoriamc.home.admin`   | Admin homes (autres joueurs) | `op`   |
| `egoriamc.home.staff`   | Voir homes (Staff)           | `op`   |
| `egoriamc.warp.use`     | Utiliser les warps           | `true` |
| `egoriamc.warp.admin`   | Créer/supprimer warps        | `op`   |
| `egoriamc.backpack.use` | Utiliser backpack            | `true` |
| `egoriamc.announce`     | Envoyer annonces             | `op`   |
| `egoriamc.furnace.use`  | Commande furnace (Gardien)   | `op`   |
| `egoriamc.craft.use`    | Commande craft (Gardien)     | `op`   |
| `egoriamc.live.use`     | Annoncer live (Streamer)     | `op`   |
| `egoriamc.warn`         | Avertir joueurs              | `op`   |
| `egoriamc.reload`       | Recharger config             | `op`   |
| `egoriamc.vote2sleep`   | Voter pour passer la nuit    | `true` |

## 📊 Dépendances externes

- **Vault** (optional) - Pour le système économique du backpack
- **MySQL Connector** - Pour les warnings en base de données

## 🔧 Dépannage

### ❌ Erreurs courantes

**Le plugin ne démarre pas**

- Vérifier Java 21+ : `java -version`
- Vérifier que Spigot/Paper 1.21.1+ est utilisé
- Vérifier les logs : `tail -f logs/latest.log`

**Les homes ne se sauvegardent pas**

- Vérifier les permissions du dossier `plugins/EgoriaMC/`
- Vérifier l'espace disque
- Vérifier que `storage: file` est configuré dans config.yml

**Le backpack refuse de déverrouiller**

- Vérifier que Vault est installé
- Vérifier que l'économie est activée
- Vérifier le solde du joueur

**Les emojis ne s'affichent pas**

- Vérifier que le resource pack est chargé côté client
- Vérifier que `use-emojis: true` dans config.yml
- Certains clients n'affichent pas les emojis custom

**Les mentions (@pseudo) ne fonctionnent pas**

- Vérifier que le pseudo est correct
- Vérifier que le joueur est en ligne
- Vérifier les logs pour les erreurs

## 📖 Guide d'utilisation

### Créer un home

```
Joueur: /home set maison
Bot: ✓ Votre home maison a été créé !
```

### Téléporter à un home

```
Joueur: /home tp maison
Bot: ✓ Téléportation vers maison...
```

### Voter pour passer la nuit

```
Joueur1: /vote2sleep
Bot: Joueur1 a voté pour passer la nuit ! (1/2)

Joueur2: /vote2sleep
Bot: ✓ La nuit a été passée ! Bonne journée !
```

### Déverrouiller un slot du backpack

1. Ouvrir le backpack : `/backpack`
2. Cliquer sur un slot verrouillé (rouge)
3. Si vous avez assez d'argent → slot déverrouillé
4. Les items se sauvegardent automatiquement

### Envoyer une annonce (Staff)

```
Staff: /annonce Le serveur redémarrera dans 5 minutes !
Bot: [ANNONCE] Le serveur redémarrera dans 5 minutes !
     (avec séparateurs et sons)
```

## 🚀 Performanzes et optimisations

- **Auto-save du backpack** : Toutes les 30 secondes
- **Cache des données** : Homes/Warps chargés en mémoire
- **Sérialisation efficace** : Base64 pour les items
- **Base de données async** : Les warnings n'impactent pas le serveur
- **Gestion mémoire** : Nettoyage automatique des données

## 📚 Développement

### Ajouter une nouvelle commande

1. Créer une classe implémentant `CommandExecutor`

```java
public class MyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        // Votre code
        return true;
    }
}
```

2. L'enregistrer dans `EgoriaMC.onEnable()`

```java
getCommand("mycommand").setExecutor(new MyCommand(this));
```

3. Ajouter dans `plugin.yml`

```yaml
commands:
  mycommand:
    description: Ma commande
    usage: /mycommand
    permission: egoriamc.mycommand
```

### Ajouter un nouvel événement

1. Créer un listener implémentant `Listener`

```java
public class MyListener implements Listener {
    @EventHandler
    public void onEvent(SomeEvent event) {
        // Votre code
    }
}
```

2. L'enregistrer dans `EgoriaMC.onEnable()`

```java
getServer().getPluginManager().registerEvents(
    new MyListener(), this);
```

## 📝 Historique des versions

- **v1.6** (27 Février 2026) - Voir RELEASE_NOTES.md
- **v1.5** - Ajout du système de backpack
- **v1.4** - Ajout du vote2sleep
- **v1.3** - Ajout des warps
- **v1.0** - Version initiale (homes, messages)

## 📄 Licence

GPL v3 - Voir le fichier [LICENSE](LICENSE)

## 💬 Support & Contribution

Pour les bugs, suggestions ou contributions :

1. Vérifier les logs du serveur
2. Consulter la documentation
3. Créer une issue sur le dépôt

## 👥 Auteurs

- **EgoriaMC** - Serveur Minecraft SMP/Survival
