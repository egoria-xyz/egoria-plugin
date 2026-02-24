package me.egoriamc.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitaire pour gérer les emojis du resource pack et les emojis Discord-like
 */
public class EmojiUtil {

    // Emojis du resource pack personnalisé
    public static final String OWNER = "ϕ";
    public static final String ADMIN = "ϖ";
    public static final String DEVELOPER = "Ͱ";
    public static final String STAFF = "ᾞ";
    public static final String VIP = "Ͳ";
    public static final String MEMBER = "ϼ";

    // Map des emojis Discord-like (:emoji:)
    private static final Map<String, String> discordEmojis = new HashMap<>();
    private static final Pattern EMOJI_PATTERN = Pattern.compile(":([a-zA-Z0-9_]+):");

    /**
     * Initialise les emojis depuis le fichier emojis.yml
     */
    public static void loadEmojis(JavaPlugin plugin) {
        File emojisFile = new File(plugin.getDataFolder(), "emojis.yml");
        
        if (!emojisFile.exists()) {
            plugin.saveResource("emojis.yml", false);
        }

        FileConfiguration emojisConfig = YamlConfiguration.loadConfiguration(emojisFile);
        
        // Charger tous les emojis depuis le fichier
        if (emojisConfig.getConfigurationSection("emojis") != null) {
            for (String key : emojisConfig.getConfigurationSection("emojis").getKeys(false)) {
                String emoji = emojisConfig.getString("emojis." + key);
                discordEmojis.put(key.toLowerCase(), emoji);
            }
        }

        plugin.getLogger().info("Chargé " + discordEmojis.size() + " emojis depuis emojis.yml");
    }

    /**
     * Remplace les codes :emoji: dans un message par les emojis Unicode
     */
    public static String replaceEmojis(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        Matcher matcher = EMOJI_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String emojiName = matcher.group(1).toLowerCase();
            String emoji = discordEmojis.get(emojiName);
            
            if (emoji != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(emoji));
            } else {
                matcher.appendReplacement(result, matcher.group(0));
            }
        }
        
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Récupère l'emoji correspondant au type (resource pack)
     */
    public static String getEmoji(String type) {
        return switch (type.toLowerCase()) {
            case "owner" -> OWNER;
            case "admin" -> ADMIN;
            case "developer" -> DEVELOPER;
            case "staff" -> STAFF;
            case "vip" -> VIP;
            case "member" -> MEMBER;
            default -> MEMBER;
        };
    }

    /**
     * Format un message avec emoji au début
     */
    public static String formatWithEmoji(String emojiType, String message) {
        return getEmoji(emojiType) + " " + message;
    }

    /**
     * Récupère la liste des emojis disponibles
     */
    public static Map<String, String> getAvailableEmojis() {
        return new HashMap<>(discordEmojis);
    }
}
