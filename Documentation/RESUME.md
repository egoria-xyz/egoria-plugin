# Résumé du Projet - EgoriaMC Plugin

## ✅ Checklist de Réalisation

### Fonctionnalités Implémentées

- [x] **Gestion des homes joueur**
  - [x] Création de homes (`/home set`)
  - [x] Suppression de homes (`/home delete`)
  - [x] Listing des homes (`/home list`)
  - [x] Téléportation aux homes (`/home tp`)
  - [x] Limite configurable de homes par joueur
  - [x] Sauvegarde automatique en fichier YAML

- [x] **Gestion des messages**
  - [x] Messages de join configurables
  - [x] Messages de leave configurables
  - [x] Messages de death configurables
  - [x] Tous les messages avec support des couleurs
  - [x] Intégration des emojis du resource pack

- [x] **Système d'emojis**
  - [x] 6 emojis disponibles (owner, admin, developer, staff, vip, member)
  - [x] Intégration automatique dans les messages
  - [x] Support dans les logs console
  - [x] Utilitaire EmojiUtil pour faciliter l'usage

- [x] **Configuration**
  - [x] Fichier config.yml personnalisable
  - [x] Fichier messages.yml personnalisable
  - [x] Nombre de homes configurable
  - [x] Activité des emojis configurable

- [x] **Code Propre**
  - [x] Architecture modulaire avec managers
  - [x] Séparation des responsabilités
  - [x] Nommage cohérent
  - [x] Logging avancé
  - [x] Gestion d'erreurs appropriée
  - [x] Support Java 21

## 📂 Structure du Projet

```
egoriamc-bukkit/
├── src/main/
│   ├── java/me/egoriamc/         # Code source Java
│   │   ├── EgoriaMC.java         # Classe principale
│   │   ├── util/
│   │   │   └── EmojiUtil.java
│   │   ├── manager/
│   │   │   ├── ConfigManager.java
│   │   │   ├── MessageManager.java
│   │   │   └── HomeManager.java
│   │   ├── command/
│   │   │   └── HomeCommand.java
│   │   └── listener/
│   │       └── PlayerEventListener.java
│   └── resources/                # Fichiers config
│       ├── plugin.yml
│       ├── config.yml
│       └── messages.yml
├── pom.xml                        # Configuration Maven
├── README.md                      # Documentation principale
├── INSTALLATION.md                # Guide d'installation
├── DEPLOIEMENT.md                 # Guide de déploiement
├── ARCHITECTURE.md                # Documentation technique
├── EXEMPLES.md                    # Exemples de configuration
└── target/
    └── egoriamc-plugin-1.0-SNAPSHOT.jar  # Plugin compilé
```

## 📊 Statistiques du Projet

| Métrique                  | Valeur  |
| ------------------------- | ------- |
| Fichiers Java             | 7       |
| Lignes de code            | ~700    |
| Classes                   | 7       |
| Interfaces implémentées   | 2       |
| Fichiers de configuration | 3       |
| Fichiers de documentation | 5       |
| Taille du JAR             | 19.5 KB |
| Version Java              | 21      |
| Version Bukkit            | 1.21.1+ |

## 🎯 Classes et Responsabilités

| Classe                | Responsabilité                                          |
| --------------------- | ------------------------------------------------------- |
| `EgoriaMC`            | Point d'entrée, initialisation, gestion du cycle de vie |
| `ConfigManager`       | Chargement et gestion des fichiers de configuration     |
| `MessageManager`      | Gestion des messages avec placeholders et emojis        |
| `HomeManager`         | Gestion des homes (CRUD, cache, sauvegarde)             |
| `HomeCommand`         | Implémentation de la commande `/home`                   |
| `PlayerEventListener` | Écoute des événements de joueurs                        |
| `EmojiUtil`           | Utilitaire pour l'accès aux emojis du resource pack     |

## 🔧 Caractéristiques Techniques

### Gestion de Configuration

- Réchargement au démarrage
- Sauvegarde asynchrone
- Support YAML natif Bukkit
- Validation des paramètres

### Gestion des Homes

- Cache en mémoire pour performance
- Sauvegarde en fichier YAML
- Limitation par joueur
- Validation des noms

### Gestion des Événements

- PlayerJoinEvent
- PlayerQuitEvent
- PlayerDeathEvent avec analyse de cause

### Permissions

- `egoriamc.home.use` - Utiliser les homes
- `egoriamc.home.admin` - Admin homes

## 📋 Fonctionnalités Avancées

- ✅ Support des emojis personnalisés du resource pack
- ✅ Messages avec codes couleur Bukkit
- ✅ Placeholders automatiques ({player}, {name}, {cause}, etc.)
- ✅ Sauvegarde asynchrone des données
- ✅ Cache en mémoire pour les homes
- ✅ Gestion des erreurs robuste
- ✅ Logging détaillé avec emojis
- ✅ Validation des entrées utilisateur

## 🚀 Comment Utiliser

### Installation Rapide

```bash
# 1. Compiler
mvn clean package

# 2. Copier le JAR
copy target\egoriamc-plugin-1.0-SNAPSHOT.jar <serveur>\plugins\

# 3. Redémarrer le serveur
```

### Utilisation en Jeu

```
/home set maison          # Créer un home
/home list               # Lister les homes
/home tp maison          # Aller à un home
/home delete maison      # Supprimer un home
```

## 📚 Documentation

Consulter les fichiers :

- **[README.md](README.md)** - Vue d'ensemble et guide d'utilisation
- **[INSTALLATION.md](INSTALLATION.md)** - Guide d'installation détaillé
- **[DEPLOIEMENT.md](DEPLOIEMENT.md)** - Guide de déploiement sur serveur
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Architecture technique et points d'extension
- **[EXEMPLES.md](EXEMPLES.md)** - Exemplaires de configuration

## 🔮 Améliorations Futures Possibles

- [ ] Base de données pour stockage persistant
- [ ] Commande de reload sans redémarrage
- [ ] Support des homes publics/privés
- [ ] Limite de homes basée sur les permissions
- [ ] Historique des homes supprimés
- [ ] Partage de homes entre joueurs
- [ ] Commande pour voir les homes des autres
- [ ] Animation de téléportation
- [ ] Sons personnalisés
- [ ] Warp publics gérés par les admins

## 🐛 Points d'Attention

- La limite de 3 homes par défaut peut être ajustée dans config.yml
- Les homes se sauvent autom dans homes.yml
- Les permissions par défaut : everyone peut créer des homes
- Les emojis requièrent le resource pack personnalisé
- Supports Java 21+ (compilé) / Java 17+ (exécution)

## ✨ Highlights du Code

### Pattern Manager

Le plugin utilise le pattern Manager pour une séparation claire des responsabilités :

- ConfigManager : Configuration
- MessageManager : Messages et localisation
- HomeManager : Logique métier des homes

### Utilitaire Emojis

`EmojiUtil` fournit une abstraction simple pour les emojis :

```java
// Utilisation simple
EmojiUtil.getEmoji("member");
EmojiUtil.formatWithEmoji("admin", message);
```

### Sauvegarde Asynchrone

Les données se sauvent de manière asynchrone pour ne pas bloquer le serveur :

```java
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    // Sauvegarde
});
```

### Validation d'Entrée

Les noms de homes sont validés :

```
- Longueur max: 16 caractères
- Caractères autorisés: a-z, 0-9, -, _
```

## 📦 Dépendances

- **Bukkit API 1.21.1** (fourni par le serveur)
- Pas de dépendances externes

## 🎉 Conclusion

Le plugin **EgoriaMC** est une solution complète, propre et extensible pour la gestion des homes et des messages sur un serveur Bukkit 1.21.1+.

Prêt à être déployé en production ! 🚀
