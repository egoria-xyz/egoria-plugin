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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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

    private boolean isWarpListGui(Inventory inv) {
        if (inv == null || inv.getSize() != INVENTORY_SIZE)
            return false;
        ItemStack s0 = inv.getItem(0);
        if (s0 == null || s0.getType() != Material.COMPASS) {
            return false; // Ce n'est pas la GUI des warps
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory topInv = event.getView().getTopInventory();

        if (!isWarpListGui(topInv)) {
            return;
        }

        // Bloquer TOUS les clics immediatement
        event.setCancelled(true);

        Inventory clickedInv = event.getClickedInventory();
        if (!isWarpListGui(clickedInv)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();

        // Bouton precedent (slot 25)
        if (slot == 25 && clicked.getType() == Material.ARROW) {
            handlePreviousPageClick(player, clickedInv);
            return;
        }

        // Bouton suivant (slot 26)
        if (slot == 26 && clicked.getType() == Material.ARROW) {
            handleNextPageClick(player, clickedInv);
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

        // Fermer l'inventaire
        player.closeInventory();

        // Exécuter la commande /warp
        player.performCommand("warp " + warpName);
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
     * Bloque le glisser-deposer dans la GUI des warps
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isWarpListGui(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    /**
     * Bloque les clics en dehors de la GUI (cursor clicks)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryCursorClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            if (isWarpListGui(event.getView().getTopInventory())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Nettoie les données du joueur à la déconnexion
     */
    public static void cleanupPlayer(String playerId) {
        PLAYER_WARP_PAGES.remove(playerId);
    }
}
