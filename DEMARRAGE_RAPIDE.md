# Démarrage Rapide - EgoriaMC Plugin

## Compilé en 30 secondes

### Prérequis

- Windows/Linux/Mac
- Java 21 installé
- Maven installé

### 3 étapes super rapides

#### 1️⃣ Compiler

```bash
cd g:\Git\egoriamc-bukkit
mvn clean package
```

✅ Le fichier JAR est créé dans `target/`

#### 2️⃣ Copier sur le serveur

```bash
copy target\egoriamc-plugin-1.0-SNAPSHOT.jar <votre-serveur>\plugins\
```

#### 3️⃣ Redémarrer le serveur

Le serveur créera automatiquement :

```
plugins/
  EgoriaMC/
    ├── config.yml      (configuration)
    ├── messages.yml    (messages personnalisables)
    └── homes.yml       (données des homes)
```

## En Jeu : 5 Commandes

```
/home              # Téléporter au premier home
/home set test     # Créer un home nommé "test"
/home list         # Voir tous vos homes
/home tp test      # Aller au home "test"
/home delete test  # Supprimer le home "test"
```

## Configuration en 2 Minutes

### Augmenter le nombre de homes

Ouvrir `plugins/EgoriaMC/config.yml` :

```yaml
homes:
  max-homes: 5 # Changer 3 à 5 (ou plus)
```

### Changer les messages de bienvenue

Ouvrir `plugins/EgoriaMC/messages.yml` :

```yaml
join:
  message: "&6Bienvenue &e{player} &6!"
  emoji: "vip"
```

## Tester en 30 secondes

1. Se connecter au serveur
2. `/home set base` → Créer un home
3. Aller ailleurs
4. `/home tp base` → Téléportation !
5. `/home list` → Voir le home

## Fichiers Importants

| Fichier                            | Quoi ?                     |
| ---------------------------------- | -------------------------- |
| [README.md](README.md)             | Vue générale du plugin     |
| [INSTALLATION.md](INSTALLATION.md) | Installation détaillée     |
| [DEPLOIEMENT.md](DEPLOIEMENT.md)   | Guide serveur production   |
| [EXEMPLES.md](EXEMPLES.md)         | Exemples de configuration  |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Comment le code fonctionne |

## Permissions (par défaut tout le monde peut)

```yaml
# Dans server.properties ou plugin du serveur
egoriamc.home.use: true # Utiliser les homes
egoriamc.home.admin: op # Admin les homes
```

## Problèmes Courants

**❌ "Plugin not found"**
→ Vérifier que le JAR est dans `plugins/`

**❌ Commande ne fonctionne pas**
→ Vérifier avec `/plugins` que EgoriaMC est chargé
→ Vérifier les permissions : `/perms check @s egoriamc.home.use`

**❌ Emojis ne s'affichent pas**
→ Assurez-vous que le resource pack est activé côté client

**❌ Les homes ne se sauvegardent pas**
→ Vérifier l'espace disque
→ Vérifier que dossier `plugins/EgoriaMC/` est accessible

## Emojis Disponibles

| Type      | Emoji | Utilisation          |
| --------- | ----- | -------------------- |
| owner     | ϕ     | Propriétaire serveur |
| admin     | ϖ     | Administrateur       |
| developer | Ͱ     | Développeur          |
| staff     | ᾞ     | Staff                |
| vip       | Ͳ     | Joueur VIP           |
| member    | ϼ     | Joueur normal        |

**Utilisation dans messages.yml** :

```yaml
join:
  emoji: "vip" # Utilisera l'emoji Ͳ
```

## Prochaines Étapes

### Pour personnaliser davantage

👉 Voir [EXEMPLES.md](EXEMPLES.md)

### Pour déployer sur un serveur production

👉 Voir [DEPLOIEMENT.md](DEPLOIEMENT.md)

### Pour comprendre l'architecture

👉 Voir [ARCHITECTURE.md](ARCHITECTURE.md)

## Support Rapide

En cas de problème :

1. Vérifier les **logs du serveur**

   ```
   tail -f logs/latest.log | grep EgoriaMC
   ```

2. Vérifier le **fichier config**

   ```
   plugins/EgoriaMC/config.yml
   ```

3. Consulter la **documentation**
   Tous les guides sont en français dans ce dépôt 📖

---

**C'est tout ! Vous pouvez commencer à utiliser le plugin maintenant.** 🚀
