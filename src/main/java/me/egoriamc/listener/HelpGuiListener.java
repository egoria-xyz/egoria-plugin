package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.HelpGuiManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

/**
 * Listener pour la GUI d'aide
 */
public class HelpGuiListener implements Listener {

    private final HelpGuiManager helpGuiManager;

    public HelpGuiListener(HelpGuiManager helpGuiManager) {
        this.helpGuiManager = helpGuiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();
        if (!title.contains("Aide") && !title.contains("Staff") && !title.contains("Admin")) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        String itemName = clicked.getItemMeta() != null ? clicked.getItemMeta().getDisplayName() : "";

        // Aide principale
        if (title.contains("Aide du Plugin")) {
            if (itemName.contains("STAFF")) {
                helpGuiManager.openStaffGui(player);
            } else if (itemName.contains("ADMIN")) {
                helpGuiManager.openAdminGui(player);
            } else if (itemName.contains("Fermer")) {
                player.closeInventory();
            }
        }

        // GUI Staff
        if (title.contains("Commandes Staff")) {
            if (itemName.contains("Retour")) {
                helpGuiManager.openHelpGui(player);
            }
        }

        // GUI Admin
        if (title.contains("Commandes Admin")) {
            if (itemName.contains("Retour")) {
                helpGuiManager.openHelpGui(player);
            }
        }
    }
}
