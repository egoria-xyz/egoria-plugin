package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.WarpListGuiManager;
import me.egoriamc.manager.WarpManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Listener pour la GUI de la liste des warps
 */
public class WarpListGuiListener implements Listener {

    private static final String WARP_LIST_TITLE_PREFIX = "§e🌍 Warps";
    private static final int INVENTORY_SIZE = 27;
    private static final Map<String, Integer> PLAYER_WARP_PAGES = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getSize() != INVENTORY_SIZE) {
            return;
        }

        // Vérifier si c'est bien la GUI des warps en regardant si les slots ont les
        // bons items
        ItemStack slot0 = inventory.getItem(0);
        if (slot0 == null || slot0.getType() != Material.COMPASS) {
            return; // Ce n'est pas la GUI des warps
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();

        // Bouton précédent (slot 25)
        if (slot == 25 && clicked.getType() == Material.ARROW) {
            handlePreviousPageClick(player, inventory);
            return;
        }

        // Bouton suivant (slot 26)
        if (slot == 26 && clicked.getType() == Material.ARROW) {
            handleNextPageClick(player, inventory);
            return;
        }

        // Clic sur un warp (slot 0-24)
        if (slot < 25 && clicked.getType() == Material.COMPASS) {
            handleWarpClick(player, clicked);
        }
    }

    /**
     * Gère le clic sur un warp
     */
    private void handleWarpClick(Player player, ItemStack warpItem) {
        if (warpItem.getItemMeta() == null) {
            return;
        }

        String warpName = warpItem.getItemMeta().getDisplayName();
        // Enlever la couleur et l'emoji
        warpName = warpName.replace("§e🌍 ", "").replace("§e", "").replace("§f", "");

        EgoriaMC plugin = EgoriaMC.getInstance();
        WarpManager warpManager = plugin.getWarpManager();
        MessageManager messageManager = plugin.getMessageManager();

        Location warp = warpManager.getWarp(warpName);

        if (warp == null) {
            player.sendMessage(messageManager.getWarpNotFound());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 0.5f);
            return;
        }

        // Vérifier la permission
        if (!player.hasPermission("egoriamc.warp.use")) {
            player.sendMessage(messageManager.translateColors("&cVous n'avez pas la permission."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 0.5f);
            return;
        }

        player.teleport(warp);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.sendMessage(messageManager.getWarpTpSuccess(warpName));
        plugin.logInfo(
                messageManager.translateColors("&a" + player.getName() + " &es'est téléporté au warp: &b" + warpName));

        player.closeInventory();
    }

    /**
     * Gère le clic sur le bouton précédent
     */
    private void handlePreviousPageClick(Player player, Inventory inventory) {
        String playerId = player.getUniqueId().toString();
        int currentPage = PLAYER_WARP_PAGES.getOrDefault(playerId, 1);

        if (currentPage <= 1) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.5f);
            return;
        }

        int newPage = currentPage - 1;
        PLAYER_WARP_PAGES.put(playerId, newPage);

        Inventory newGui = WarpListGuiManager.createWarpListInventory(newPage);
        player.openInventory(newGui);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
    }

    /**
     * Gère le clic sur le bouton suivant
     */
    private void handleNextPageClick(Player player, Inventory inventory) {
        String playerId = player.getUniqueId().toString();
        int currentPage = PLAYER_WARP_PAGES.getOrDefault(playerId, 1);

        // Calculer le nombre total de pages
        WarpManager warpManager = EgoriaMC.getInstance().getWarpManager();
        int totalWarps = warpManager.getAllWarps().size();
        int totalPages = (int) Math.ceil((double) totalWarps / 25);

        if (currentPage >= totalPages) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.5f);
            return;
        }

        int newPage = currentPage + 1;
        PLAYER_WARP_PAGES.put(playerId, newPage);

        Inventory newGui = WarpListGuiManager.createWarpListInventory(newPage);
        player.openInventory(newGui);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
    }

    /**
     * Retourne la page actuelle du joueur
     */
    public static int getPlayerPage(String playerId) {
        return PLAYER_WARP_PAGES.getOrDefault(playerId, 1);
    }

    /**
     * Définit la page du joueur
     */
    public static void setPlayerPage(String playerId, int page) {
        PLAYER_WARP_PAGES.put(playerId, Math.max(1, page));
    }

    /**
     * Nettoie les données du joueur à la déconnexion
     */
    public static void cleanupPlayer(String playerId) {
        PLAYER_WARP_PAGES.remove(playerId);
    }
}
