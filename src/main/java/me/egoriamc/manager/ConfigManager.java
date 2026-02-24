package me.egoriamc.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Gestionnaire de configuration
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private final File pluginDirectory;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginDirectory = plugin.getDataFolder();

        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }

        reload();
    }

    public void reload() {
        // Charger config.yml
        File configFile = new File(pluginDirectory, "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);

        // Charger messages.yml
        File messagesFile = new File(pluginDirectory, "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public int getMaxHomes() {
        return config.getInt("homes.max-homes", 3);
    }

    public boolean useEmojis() {
        return config.getBoolean("logging.use-emojis", true);
    }

    public String getDefaultEmoji() {
        return config.getString("logging.default-emoji", "member");
    }

    /**
     * Retourne la liste de tous les types d'emojis disponibles
     */
    public String[] getAvailableEmojis() {
        return new String[]{"owner", "admin", "developer", "staff", "vip", "member"};
    }

    /**
     * Retourne un String formaté avec tous les emojis disponibles
     */
    public String getAvailableEmojisAsString() {
        return String.join(", ", getAvailableEmojis());
    }
}
