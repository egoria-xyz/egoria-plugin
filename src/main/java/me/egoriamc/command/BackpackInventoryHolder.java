package me.egoriamc.command;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/**
 * Holder pour l'inventaire du backpack
 */
public class BackpackInventoryHolder implements InventoryHolder {

    private final UUID playerUuid;

    public BackpackInventoryHolder(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
