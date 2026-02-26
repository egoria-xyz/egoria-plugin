package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestion des warns en BDD (table warns).
 */
public class WarnManager {

    private static final String INSERT = "INSERT INTO warns (username, discord_id, reason, warner, server_name) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_NO_DISCORD = "INSERT INTO warns (username, reason, warner, server_name) VALUES (?, ?, ?, ?)";

    private final EgoriaMC plugin;
    private final DatabaseManager databaseManager;

    public WarnManager(EgoriaMC plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }

    /**
     * Enregistre un warn (insertion asynchrone en BDD).
     * discord_id peut être récupéré plus tard depuis la table users si besoin.
     */
    public void addWarn(String username, String reason, String warnerName, String serverName) {
        if (!databaseManager.isConfigured()) {
            return;
        }
        databaseManager.runAsync(() -> {
            try (Connection conn = databaseManager.getConnection()) {
                String discordId = resolveDiscordId(conn, username);
                if (discordId != null) {
                    try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                        ps.setString(1, username);
                        ps.setString(2, discordId);
                        ps.setString(3, reason);
                        ps.setString(4, warnerName);
                        ps.setString(5, serverName);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(INSERT_NO_DISCORD)) {
                        ps.setString(1, username);
                        ps.setString(2, reason);
                        ps.setString(3, warnerName);
                        ps.setString(4, serverName);
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                plugin.logError("Erreur enregistrement warn: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Tente de récupérer discord_id depuis une table users si elle existe.
     * Adapter le nom de la colonne (minecraft_username / username) selon votre schéma.
     */
    private String resolveDiscordId(Connection conn, String username) {
        try {
            // Essai avec une table users courante (minecraft_username -> discord_id)
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT discord_id FROM users WHERE minecraft_username = ? LIMIT 1")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("discord_id");
                }
            }
        } catch (SQLException ignored) {
            // Table users absente ou colonnes différentes : on laisse discord_id NULL
        }
        return null;
    }

    /**
     * Récupère les warns d'un joueur (synchrone, à appeler en async si besoin).
     */
    public List<WarnEntry> getWarns(String username) throws SQLException {
        List<WarnEntry> list = new ArrayList<>();
        if (!databaseManager.isConfigured()) {
            return list;
        }
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, username, discord_id, created_at, reason, warner FROM warns WHERE username = ? ORDER BY created_at DESC")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new WarnEntry(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("discord_id"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toInstant() : null,
                        rs.getString("reason"),
                        rs.getString("warner")
                ));
            }
        }
        return list;
    }

    public static final class WarnEntry {
        public final int id;
        public final String username;
        public final String discordId;
        public final java.time.Instant createdAt;
        public final String reason;
        public final String warner;

        public WarnEntry(int id, String username, String discordId, java.time.Instant createdAt, String reason, String warner) {
            this.id = id;
            this.username = username;
            this.discordId = discordId;
            this.createdAt = createdAt;
            this.reason = reason;
            this.warner = warner;
        }
    }
}
