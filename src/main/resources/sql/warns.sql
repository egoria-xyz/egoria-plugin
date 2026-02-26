-- Base de données : egoriamc
-- Table des avertissements (warns) pour la commande /warn
-- Exécuter ce script sur la base après création de la base.

-- Table des warns
CREATE TABLE IF NOT EXISTS warns (
    id          INT             NOT NULL AUTO_INCREMENT,
    username    VARCHAR(36)     NOT NULL COMMENT 'Pseudo Minecraft (ou UUID selon liaison)',
    discord_id  VARCHAR(20)     NULL     COMMENT 'ID Discord, à récupérer depuis la table users si besoin',
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason      VARCHAR(500)    NOT NULL COMMENT 'Raison du warn',
    warner      VARCHAR(36)     NULL     COMMENT 'Pseudo ou UUID du staff qui a donné le warn',
    server_name VARCHAR(64)     NULL     COMMENT 'Nom du serveur si multi-serveurs',
    PRIMARY KEY (id),
    INDEX idx_username (username),
    INDEX idx_discord_id (discord_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Exemple : si vous avez une table users avec discord_id et username Minecraft,
-- vous pouvez remplir discord_id à l'insertion ou via une requête :
--
-- INSERT INTO warns (username, discord_id, reason, warner)
-- SELECT :username, u.discord_id, :reason, :warner
-- FROM users u WHERE u.minecraft_username = :username LIMIT 1;
--
-- Ou laisser discord_id NULL et le remplir plus tard par un job/trigger.
