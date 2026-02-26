package me.egoriamc.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

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
            handlePreviousPageClick(player, event.getView().getTitle());
        } else if (slot == 26) {
            // Bouton Prochaine Page
            handleNextPageClick(player, event.getView().getTitle());
        }
        // Les items de plugins ne font rien, juste l'affichage
    }

    /**
     * Gère le clic sur le bouton Page Précédente
     */
    private void handlePreviousPageClick(Player player, String title) {
        int currentPage = extractPageNumber(title);
        if (currentPage > 1) {
            // Ajouter un feedback sonore
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            // Exécuter la commande pour aller à la page précédente
            player.performCommand("plugins " + (currentPage - 1));
        } else {
            // Son d'erreur si on est déjà à la première page
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.5f);
        }
    }

    /**
     * Gère le clic sur le bouton Prochaine Page
     */
    private void handleNextPageClick(Player player, String title) {
        String[] parts = title.split("/");
        if (parts.length >= 2) {
            try {
                int totalPages = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                int currentPage = extractPageNumber(title);
                if (currentPage < totalPages) {
                    // Ajouter un feedback sonore
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    // Exécuter la commande pour aller à la page suivante
                    player.performCommand("plugins " + (currentPage + 1));
                } else {
                    // Son d'erreur si on est déjà à la dernière page
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.5f);
                }
            } catch (NumberFormatException ignored) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 0.5f);
            }
        }
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

    /**
     * Extrait le numéro de page du titre de l'inventaire
     */
    private int extractPageNumber(String title) {
        try {
            String[] parts = title.split("/");
            if (parts.length >= 1) {
                String pagePart = parts[0];
                return Integer.parseInt(pagePart.replaceAll("[^0-9]", ""));
            }
        } catch (NumberFormatException ignored) {
        }
        return 1;
    }
}
