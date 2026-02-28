package me.egoriamc.listener;

import me.egoriamc.manager.BalanceTopGuiManager;
import me.egoriamc.manager.BalanceTopPageManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Listener pour la GUI du leaderboard d'argent
 */
public class BalanceTopGuiListener implements Listener {

    private static final String BALTOP_INVENTORY_TITLE = "§e💰 Top Argent";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Vérifier si c'est l'inventaire du baltop
        if (!isBalanceTopInventory(event.getView().getTitle())) {
            return;
        }

        // Empêcher toute interaction
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
    }

    /**
     * Gère le clic sur le bouton Page Précédente
     */
    private void handlePreviousPageClick(Player player) {
        int currentPage = BalanceTopPageManager.getPlayerPage(player.getUniqueId());
        if (currentPage > 1) {
            // Ajouter un feedback sonore
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

            // Mettre à jour directement l'inventaire
            int newPage = currentPage - 1;
            BalanceTopPageManager.setPlayerPage(player.getUniqueId(), newPage);

            Inventory newInventory = BalanceTopGuiManager.createBalanceTopInventory(newPage);
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
        int currentPage = BalanceTopPageManager.getPlayerPage(player.getUniqueId());

        // On ne peut pas calculer le nombre de pages sans passer par la création de
        // l'inventaire
        // Donc on essaie simplement d'aller à la page suivante
        // Si cette page n'existe pas, on reste à la page actuelle

        // Ajouter un feedback sonore
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

        // Mettre à jour directement l'inventaire
        int newPage = currentPage + 1;
        BalanceTopPageManager.setPlayerPage(player.getUniqueId(), newPage);

        Inventory newInventory = BalanceTopGuiManager.createBalanceTopInventory(newPage);
        player.openInventory(newInventory);
    }

    /**
     * Vérifier si l'inventaire est le baltop
     */
    private boolean isBalanceTopInventory(String title) {
        return title.contains(BALTOP_INVENTORY_TITLE);
    }
}
