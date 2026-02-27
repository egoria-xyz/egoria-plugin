package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.command.BackpackCommand;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

/**
 * Listener pour les interactions avec le backpack
 */
public class BackpackInventoryListener implements Listener {

    private final EgoriaMC plugin;

    public BackpackInventoryListener(EgoriaMC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Vérifier si c'est le backpack
        if (!isBackpackInventory(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(false);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();
        ItemStack clickedItem = event.getCurrentItem();

        // Vérifier que le clic n'est pas sur un slot invalide
        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        // Vérifier si c'est un slot verrouillé (BARRIER = prochain slot à
        // déverrouiller)
        if (clickedItem != null && clickedItem.getType() == Material.BARRIER) {
            event.setCancelled(true);

            // Essayer de déverrouiller le slot
            BackpackCommand command = new BackpackCommand(plugin);
            // Charger les données du backpack pour éviter les inconsistances
            plugin.getBackpackManager().loadBackpackData(player.getUniqueId());
            if (command.unlockSlot(player, slot)) {
                // Sauvegarder l'inventaire avant de le rafraîchir
                command.saveBackpackInventory(player);
                // Mettre à jour l'inventaire
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.openInventory(command.createBackpackInventory(player));
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Sauvegarder le backpack quand on le ferme
        if (isBackpackInventory(event.getView().getTitle())) {
            Player player = (Player) event.getPlayer();
            BackpackCommand command = new BackpackCommand(plugin);
            command.saveBackpackInventory(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Vérifier si c'est le backpack
        if (!isBackpackInventory(event.getView().getTitle())) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Vérifier que le drag ne se fait pas sur les slots verrouillés ou désactivés
        for (int slot : event.getRawSlots()) {
            if (slot < event.getInventory().getSize()) {
                ItemStack item = event.getInventory().getItem(slot);
                if (item != null
                        && (item.getType() == Material.BARRIER || item.getType() == Material.GRAY_STAINED_GLASS_PANE)) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
                    return;
                }
            }
        }
    }

    /**
     * Vérifie si le titre est celui d'un backpack
     */
    private boolean isBackpackInventory(String title) {
        return title != null && title.contains("Backpack");
    }
}
