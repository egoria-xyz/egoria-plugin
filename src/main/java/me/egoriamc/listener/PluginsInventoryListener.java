package me.egoriamc.listener;

import me.egoriamc.command.PluginsCommand;
import me.egoriamc.manager.PluginsPageManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

/**
 * Listener pour les interactions avec l'inventaire des plugins
 */
public class PluginsInventoryListener implements Listener {

    private static final String PLUGINS_INVENTORY_TITLE = "§ePlugins du Serveur";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Vérifier si c'est l'inventaire des plugins
        if (!isPluginsInventory(event.getView().getTitle())) {
            return;
        }

        // Empêcher toute interaction (prendre les items, etc.)
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Vérifier que le clic n'est pas sur un slot invalide
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 27) {
            return;
        }

        // Gérer les clics sur les boutons de navigation
        if (slot == 25) {
            // Bouton Page Précédente
            handlePreviousPageClick(player);
        } else if (slot == 26) {
            // Bouton Prochaine Page
            handleNextPageClick(player);
        }
        // Les items de plugins ne font rien, juste l'affichage
    }

    /**
     * Gère le clic sur le bouton Page Précédente
     */
    private void handlePreviousPageClick(Player player) {
        int currentPage = PluginsPageManager.getPlayerPage(player.getUniqueId());
        if (currentPage > 1) {
            // Ajouter un feedback sonore
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

            // Mettre à jour directement l'inventaire
            int newPage = currentPage - 1;
            PluginsPageManager.setPlayerPage(player.getUniqueId(), newPage);

            Inventory newInventory = PluginsCommand.createPluginsInventory(newPage);
            player.openInventory(newInventory);
        } else {
            // Son d'erreur si on est déjà à la première page
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.5f);
        }
    }

    /**
     * Gère le clic sur le bouton Prochaine Page
     */
    private void handleNextPageClick(Player player) {
        int currentPage = PluginsPageManager.getPlayerPage(player.getUniqueId());

        // Calculer le nombre total de pages
        int totalPages = getTotalPages();

        if (currentPage < totalPages) {
            // Ajouter un feedback sonore
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

            // Mettre à jour directement l'inventaire
            int newPage = currentPage + 1;
            PluginsPageManager.setPlayerPage(player.getUniqueId(), newPage);

            Inventory newInventory = PluginsCommand.createPluginsInventory(newPage);
            player.openInventory(newInventory);
        } else {
            // Son d'erreur si on est déjà à la dernière page
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.5f);
        }
    }

    /**
     * Calcule le nombre total de pages
     */
    private int getTotalPages() {
        int totalPlugins = org.bukkit.Bukkit.getPluginManager().getPlugins().length;
        return Math.max(1, (int) Math.ceil((double) totalPlugins / 25.0));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Vérifier si c'est l'inventaire des plugins
        if (!isPluginsInventory(event.getView().getTitle())) {
            return;
        }

        // Empêcher le drag and drop
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        // Vérifier si le joueur a un inventaire de plugins ouvert
        if (isPluginsInventory(player.getOpenInventory().getTitle())) {
            // Empêcher de dropper des items en ayant l'inventaire des plugins ouvert
            event.setCancelled(true);
        }
    }

    /**
     * Vérifie si le titre appartient à l'inventaire des plugins
     */
    private boolean isPluginsInventory(String title) {
        return title != null && title.contains(PLUGINS_INVENTORY_TITLE);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Nettoyer les données de pagination quand un joueur quitte
        PluginsPageManager.cleanupPlayer(event.getPlayer().getUniqueId());
    }
}
