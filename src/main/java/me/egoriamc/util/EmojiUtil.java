package me.egoriamc.util;

/**
 * Utilitaire pour gérer les emojis du resource pack
 */
public class EmojiUtil {

    // Emojis du resource pack personnalisé
    public static final String OWNER = "ϕ";
    public static final String ADMIN = "ϖ";
    public static final String DEVELOPER = "Ͱ";
    public static final String STAFF = "ᾞ";
    public static final String VIP = "Ͳ";
    public static final String MEMBER = "ϼ";

    /**
     * Récupère l'emoji correspondant au type
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
}
