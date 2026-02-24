# Exemples de Configuration - EgoraIMC Plugin

## Messages de Bienvenue Personnalisés

### Exemple 1 : Messages simples
```yaml
join:
  message: "&6{player} &erejoint le serveur"
  emoji: "member"

leave:
  message: "&6{player} &ea quitté le serveur"
  emoji: "member"
```

Résultat console :
```
ϼ 6Player123 erejoint le serveur
ϼ 6Player123 ea quitté le serveur
```

### Exemple 2 : Messages avec style
```yaml
join:
  message: "&b━━━━ &6{player} &bestà arrivé! &b━━━━"
  emoji: "vip"

leave:
  message: "&7[&c-&7] &6{player} &ca quitté"
  emoji: "admin"
```

### Exemple 3 : Messages spécifiques aux rôles (à faire manuellement)
```yaml
# Pour les VIP
join:
  message: "&6VIP &e{player} &6est arrivé en grand style!"
  emoji: "vip"

# Pour les staff
leave:
  message: "&cStaff &6{player} &ca terminé sa session"
  emoji: "staff"
```

## Messages de Mort

### Exemple 1 : Messages basiques
```yaml
death:
  message: "&c{player} &eest mort &7({cause})"
  by-player: "&c{player} &ea été tué par &c{killer}"
  emoji: "developer"
```

### Exemple 2 : Messages détaillés avec style
```yaml
death:
  message: "&c ✘ {player} &eest mort &7({cause})"
  by-player: "&c{player} &ea été vaincu par &c{killer}"
  lava: "&c{player} &ea brûlé vif dans la lave"
  void: "&c{player} &eest tombé dans le vide..."
  emoji: "developer"
```

### Exemple 3 : Messages fleuris
```yaml
death:
  message: "&7[&cDécès&7] &6{player} &7- Cause: &e{cause}"
  by-player: "&7[&cPVP&7] &6{killer} &c❯ &6{player}"
  emoji: "member"
```

## Configurations des Homes

### Exemple 1 : Limite basse (serveur de test)
```yaml
homes:
  max-homes: 1
  storage: file
```

### Exemple 2 : Limite moyenne (serveur classique)
```yaml
homes:
  max-homes: 5
  storage: file
```

### Exemple 3 : Limite haute (serveur VIP)
```yaml
homes:
  max-homes: 10
  storage: file
```

### Messages de homes personnalisés

#### Exemple 1 : Messages courts
```yaml
homes:
  set-success: "&a✓ {name}"
  delete-success: "&cX {name}"
  tp-success: "&a➜ {name}"
  not-found: "&c✗ Home introuvable"
  limit-reached: "&cMax homes: {max}"
```

#### Exemple 2 : Messages détaillés
```yaml
homes:
  set-success: "&aVotre home &e{name}&a a été créé avec succès!"
  delete-success: "&aLe home &e{name}&a a été supprimé!"
  tp-success: "&aTéléportation vers &e{name}&a..."
  not-found: "&cCe home n'existe pas"
  limit-reached: "&cVous avez atteint la limite: &e{max} &chomes"
  list-header: "&e╔═══ Vos Homes ═══╗"
  list-item: "&e║ &a{name} &7({world})"
```

#### Exemple 3 : Avec emojis dans les messages
```yaml
homes:
  set-success: "&aVotre home &e{name} &aa été créé &a✓"
  delete-success: "&cVotre home &e{name} &ca été supprimé &c✗"
  tp-success: "&aTéléportation en cours &a➜"
  not-found: "&cCe home introuvable &c✗"
  limit-reached: "&cLimite atteinte: &e{max} &chomes max &c❌"
```

## Configuration Recommandée pour Différents Serveurs

### Serveur Vanilla Pure
```yaml
homes:
  max-homes: 3
  storage: file

logging:
  use-emojis: false
  default-emoji: "member"

join:
  message: "&6{player} &erejoint le serveur"
  emoji: "member"

leave:
  message: "&6{player} &ea quitté le serveur"
  emoji: "member"

death:
  message: "&c{player} &eest mort"
  emoji: "member"
```

### Serveur RPG
```yaml
homes:
  max-homes: 5
  storage: file

logging:
  use-emojis: true
  default-emoji: "staff"

join:
  message: "&6[Bienvenue] &e{player} &6a rejoint Azeroth!"
  emoji: "vip"

leave:
  message: "&7[Départ] &e{player} &7a quitté le monde..."
  emoji: "member"

death:
  message: "&c[Mort] &e{player} &eest tombé aux mains de &c{cause}"
  emoji: "developer"
```

### Serveur Compétitif
```yaml
homes:
  max-homes: 1
  storage: file

logging:
  use-emojis: true
  default-emoji: "admin"

join:
  message: "&b▶ &e{player} &brejoint Match"
  emoji: "admin"

leave:
  message: "&c◀ &e{player} &cquite Match"
  emoji: "admin"

death:
  message: "&c[KO] &e{player} &celiminé"
  emoji: "developer"
```

### Serveur Casual/Friendly
```yaml
homes:
  max-homes: 10
  storage: file

logging:
  use-emojis: true
  default-emoji: "member"

join:
  message: "&6★ &e{player} &6est arrivé! ★"
  emoji: "member"

leave:
  message: "&7☆ &e{player} &7à bientôt! ☆"
  emoji: "member"

death:
  message: "&e{player} &cest mort! &7({cause})"
  emoji: "member"
```

## Codes Couleur Bukkit

| Code | Couleur |
|------|---------|
| &0 | Noir |
| &1 | Bleu marine |
| &2 | Vert |
| &3 | Cyan |
| &4 | Rouge |
| &5 | Magenta |
| &6 | Orange |
| &7 | Gris clair |
| &8 | Gris foncé |
| &9 | Bleu |
| &a | Vert clair |
| &b | Cyan clair |
| &c | Rouge clair |
| &d | Rose |
| &e | Jaune |
| &f | Blanc |

| Code | Style |
|------|-------|
| &l | Gras |
| &m | Barré |
| &n | Souligné |
| &o | Italique |
| &k | Aléatoire |
| &r | Réinitialiser |

## Exemple de Message Complexe

```yaml
join:
  message: "&7&m─────&r &6Welcome &e{player}&r &7&m─────&r"
  emoji: "vip"

death:
  message: "&4&l⚰&r &c{player} &eis dead. Cause: &7{cause}"
  by-player: "&4&l⚰&r &c{player} &ewas defeated by &c{killer}"
  emoji: "developer"
```

Résultat :
```
─────── Welcome Player123 ───────
⚰ Player123 is dead. Cause: fall
⚰ Player123 was defeated by Killer
```

## Placeholders Disponibles

### Homes
- `{name}` - Nom du home
- `{world}` - Monde du home
- `{max}` - Nombre maximum de homes

### Events
- `{player}` - Nom du joueur
- `{killer}` - Nom du tueur (death)
- `{cause}` - Cause de la mort (death)
