package me.egoriamc.manager;

import me.egoriamc.util.EmojiUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire des messages personnalisables
 */
public class MessageManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getServer().getPluginManager().getPlugin("EgoriaMC")
                .toString().equals("") ? new ConfigManager(plugin) : null;
    }

    public ConfigManager getConfigManager() {
        return plugin instanceof org.bukkit.plugin.Plugin ? new ConfigManager(plugin) : configManager;
    }

    /**
     * Récupère un message et le formate
     */
    public String getMessage(String path, Object... replacements) {
        FileConfiguration messagesConfig = getConfigManager().getMessagesConfig();
        String message = messagesConfig.getString(path, path);

        if (message == null) {
            message = path;
        }

        // Remplacer les placeholders
        int index = 0;
        for (Object replacement : replacements) {
            message = message.replace("{" + index + "}", replacement.toString());
            index++;
        }

        return translateColors(message);
    }

    /**
     * Récupère un message et le formate avec des clés
     */
    public String getMessageWithMap(String path, Map<String, String> replacements) {
        FileConfiguration messagesConfig = getConfigManager().getMessagesConfig();
        String message = messagesConfig.getString(path, path);

        if (message == null) {
            message = path;
        }

        // Remplacer les placeholders nommés
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return translateColors(message);
    }

    /**
     * Traduit les codes de couleur
     */
    public String translateColors(String message) {
        return message.replace("&", "§");
    }

    /**
     * Enlève les codes de couleur d'un message
     */
    public String stripColors(String message) {
        return message.replaceAll("§[0-9a-fk-or]", "").replaceAll("&[0-9a-fk-or]", "");
    }

    // Messages pour les homes
    public String getHomeSetSuccess(String name) {
        return getMessageWithMap("homes.set-success",
                Map.of("name", name));
    }

    public String getHomeDeleteSuccess(String name) {
        return getMessageWithMap("homes.delete-success",
                Map.of("name", name));
    }

    public String getHomeTpSuccess(String name) {
        return getMessageWithMap("homes.tp-success",
                Map.of("name", name));
    }

    public String getHomeNotFound() {
        return getMessage("homes.not-found");
    }

    public String getHomeLimitReached(int max) {
        FileConfiguration messagesConfig = getConfigManager().getMessagesConfig();
        String message = messagesConfig.getString("homes.limit-reached", "");
        return translateColors(message.replace("{max}", String.valueOf(max)));
    }

    public String getHomeListEmpty() {
        return getMessage("homes.list-empty");
    }

    public String getHomeListHeader() {
        return getMessage("homes.list-header");
    }

    public String getHomeListItem(String name, String world) {
        return getMessageWithMap("homes.list-item",
                Map.of("name", name, "world", world));
    }

    // Messages pour les événements
    public String getJoinMessage(String playerName) {
        FileConfiguration messagesConfig = getConfigManager().getMessagesConfig();
        String message = messagesConfig.getString("join.message", "");
        message = message.replace("{player}", playerName);
        String emoji = messagesConfig.getString("join.emoji", "member");
        return EmojiUtil.formatWithEmoji(emoji, translateColors(message));
    }

    public String getLeaveMessage(String playerName) {
        FileConfiguration messagesConfig = getConfigManager().getMessagesConfig();
        String message = messagesConfig.getString("leave.message", "");
        message = message.replace("{player}", playerName);
        String emoji = messagesConfig.getString("leave.emoji", "member");
        return EmojiUtil.formatWithEmoji(emoji, translateColors(message));
    }

    public String getDeathMessage(String playerName, String cause, String killer) {
        FileConfiguration messagesConfig = getConfigManager().getMessagesConfig();
        String message;

        // Vérifier si c'est un kill par joueur
        if (killer != null && !killer.isEmpty()) {
            message = messagesConfig.getString("death.by-player", "");
            message = message.replace("{player}", playerName)
                    .replace("{killer}", killer);
        } else {
            message = messagesConfig.getString("death.message", "");
            message = message.replace("{player}", playerName)
                    .replace("{cause}", cause);
        }

        String emoji = messagesConfig.getString("death.emoji", "developer");
        return EmojiUtil.formatWithEmoji(emoji, translateColors(message));
    }

    // Messages pour les warps
    public String getWarpSetSuccess(String name) {
        return getMessageWithMap("warps.set-success",
                java.util.Map.of("name", name));
    }

    public String getWarpDeleteSuccess(String name) {
        return getMessageWithMap("warps.delete-success",
                java.util.Map.of("name", name));
    }

    public String getWarpTpSuccess(String name) {
        return getMessageWithMap("warps.tp-success",
                java.util.Map.of("name", name));
    }

    public String getWarpNotFound() {
        return getMessage("warps.not-found");
    }

    public String getWarpAlreadyExists(String name) {
        return getMessageWithMap("warps.already-exists",
                java.util.Map.of("name", name));
    }

    public String getWarpListEmpty() {
        return getMessage("warps.list-empty");
    }

    public String getWarpListHeader() {
        return getMessage("warps.list-header");
    }

    public String getWarpListItem(String name, String creator) {
        return getMessageWithMap("warps.list-item",
                java.util.Map.of("name", name, "creator", creator));
    }
}
