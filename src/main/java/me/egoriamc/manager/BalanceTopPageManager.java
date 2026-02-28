package me.egoriamc.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestionnaire de pagination pour le classement d'argent
 */
public class BalanceTopPageManager {

    private static final Map<UUID, Integer> PLAYER_PAGES = new HashMap<>();

    /**
     * Obtient la page actuelle du joueur
     */
    public static int getPlayerPage(UUID uuid) {
        return PLAYER_PAGES.getOrDefault(uuid, 1);
    }

    /**
     * Définit la page actuelle du joueur
     */
    public static void setPlayerPage(UUID uuid, int page) {
        PLAYER_PAGES.put(uuid, Math.max(1, page));
    }

    /**
     * Réinitialise la page du joueur à 1
     */
    public static void resetPlayerPage(UUID uuid) {
        PLAYER_PAGES.remove(uuid);
    }

    /**
     * Nettoie les données quand un joueur quitte
     */
    public static void cleanupPlayer(UUID uuid) {
        PLAYER_PAGES.remove(uuid);
    }
}
