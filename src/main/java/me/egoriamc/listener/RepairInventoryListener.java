package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.RepairManager;
import me.egoriamc.manager.RepairInventoryManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Listener pour les interactions avec l'interface de réparation
 */
public class RepairInventoryListener implements Listener {

    private final RepairManager repairManager;
    private final MessageManager messageManager;

    public RepairInventoryListener(EgoriaMC plugin) {
        this.repairManager = plugin.getRepairManager();
        this.messageManager = plugin.getMessageManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isRepairInventory(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getSlot();

        // Slot central: placer l'item
        if (slot == RepairInventoryManager.getCentralSlot()) {
            if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                event.setCancelled(false);
                return;
            }
        }

        // Bouton "Réparer avec XP"
        if (slot == RepairInventoryManager.getRepairWithXPSlot()) {
            repairWithXP(player, event.getInventory());
            return;
        }

        // Bouton "Réparer avec Minerais"
        if (slot == RepairInventoryManager.getRepairWithMineralSlot()) {
            repairWithMinerals(player, event.getInventory());
            return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!isRepairInventory(event.getView().getTitle())) {
            return;
        }

        // Optionnel: On peut ajouter une sauvegarde si nécessaire
    }

    /**
     * Répare un item avec de l'XP
     */
    private void repairWithXP(Player player, Inventory inventory) {
        ItemStack itemToRepair = inventory.getItem(RepairInventoryManager.getCentralSlot());

        if (itemToRepair == null || itemToRepair.getType() == Material.AIR) {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ Veuillez placer un item à réparer dans le slot central."));
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
            return;
        }

        if (!repairManager.isRepairable(itemToRepair)) {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ Cet item ne peut pas être réparé !"));
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
            return;
        }

        int xpRequired = repairManager.getRepairXPCost(itemToRepair);
        int playerXP = player.getTotalExperience();

        if (playerXP < xpRequired) {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ Vous n'avez pas assez d'XP ! Vous avez §6" + playerXP + " XP §c et il faut §6" + xpRequired
                            + " XP"));
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
            return;
        }

        // Réparer l'item
        if (repairManager.repairWithXP(player, itemToRepair)) {
            player.sendMessage(messageManager.translateColors(
                    "&a✅ Succès ! Votre item a été réparé pour §e" + xpRequired + " XP§a."));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            // Mettre à jour l'inventaire
            inventory.setItem(RepairInventoryManager.getCentralSlot(), itemToRepair);
        } else {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ La réparation a échoué. Veuillez réessayer."));
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
        }
    }

    /**
     * Répare un item avec des minerais
     */
    private void repairWithMinerals(Player player, Inventory inventory) {
        ItemStack itemToRepair = inventory.getItem(RepairInventoryManager.getCentralSlot());

        if (itemToRepair == null || itemToRepair.getType() == Material.AIR) {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ Veuillez placer un item à réparer dans le slot central."));
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
            return;
        }

        if (!repairManager.isRepairable(itemToRepair)) {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ Cet item ne peut pas être réparé !"));
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
            return;
        }

        // Chercher le minerai pour réparer
        Material mineralToUse = null;
        int minCost = Integer.MAX_VALUE;

        for (Material mineral : RepairManager.getRepairMaterials().keySet()) {
            if (player.getInventory().contains(mineral)) {
                int cost = repairManager.getRepairMineralCost(itemToRepair, mineral);
                long playerAmount = player.getInventory().all(mineral).values().stream()
                        .mapToInt(ItemStack::getAmount).sum();

                if (playerAmount >= cost && cost < minCost) {
                    mineralToUse = mineral;
                    minCost = cost;
                }
            }
        }

        if (mineralToUse == null) {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ Vous n'avez pas assez de minerais pour réparer cet item !"));
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
            return;
        }

        // Réparer l'item
        if (repairManager.repairWithMineral(player, itemToRepair, mineralToUse)) {
            player.sendMessage(messageManager.translateColors(
                    "&a✅ Succès ! Votre item a été réparé pour §e" + minCost + " x " + mineralToUse.name() + "§a."));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            // Mettre à jour l'inventaire
            inventory.setItem(RepairInventoryManager.getCentralSlot(), itemToRepair);
        } else {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ La réparation a échoué. Veuillez réessayer."));
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 0.5f);
        }
    }

    /**
     * Vérifie si c'est l'inventaire de réparation
     */
    private boolean isRepairInventory(String title) {
        return title.contains("⚒ Réparation");
    }
}
