package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.BackpackManager;
import me.egoriamc.manager.BackpackInventoryManager;
import me.egoriamc.manager.MessageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Commande /backpack - Ouvre le backpack du joueur
 */
public class BackpackCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;
    private final BackpackManager backpackManager;
    private final BackpackInventoryManager inventoryManager;
    private Economy economy;

    public BackpackCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.backpackManager = plugin.getBackpackManager();
        this.inventoryManager = plugin.getBackpackInventoryManager();
        setupEconomy();
    }

    /**
     * Configure l'économie Vault
     */
    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.logError("Vault n'est pas installé !");
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager()
                .getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("backpack.must-be-player"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("egoriamc.backpack.use")) {
            player.sendMessage(messageManager.getMessage("backpack.no-permission"));
            return true;
        }

        // Vérifier que Vault est disponible
        if (economy == null) {
            player.sendMessage(messageManager.getMessage("backpack.vault-not-available"));
            return true;
        }

        // Charger les données du backpack
        backpackManager.loadBackpackData(player.getUniqueId());

        // Créer et ouvrir l'inventaire
        Inventory backpack = createBackpackInventory(player);
        player.openInventory(backpack);

        return true;
    }

    /**
     * Crée l'inventaire du backpack
     */
    public Inventory createBackpackInventory(Player player) {
        int totalSlots = backpackManager.getTotalSlots(player);
        String title = "§eBackpack - " + (totalSlots / 9) + " ligne(s)";
        Inventory inventory = Bukkit.createInventory(null, totalSlots, title);

        // Charger les items existants
        Map<Integer, ItemStack> savedItems = inventoryManager.loadBackpackInventory(player.getUniqueId());

        Set<Integer> unlockedSlots = backpackManager.getUnlockedSlots(player.getUniqueId());

        // Ajouter les items et les slots verrouillés
        for (int i = 0; i < totalSlots; i++) {
            if (unlockedSlots.contains(i)) {
                // Slot déverrouillé
                ItemStack item = savedItems.getOrDefault(i, null);
                if (item != null && !item.getType().isAir()) {
                    inventory.setItem(i, item);
                }
            } else {
                // Slot verrouillé
                double price = backpackManager.getPriceForSlot(i);
                ItemStack lockedSlot = createLockedSlotItem(price);
                inventory.setItem(i, lockedSlot);
            }
        }

        return inventory;
    }

    /**
     * Crée l'item pour un slot verrouillé
     */
    private ItemStack createLockedSlotItem(double price) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lVERROUÉ");
            List<String> lore = new ArrayList<>();
            lore.add("§7Prix: §6$" + formatPrice(price));
            lore.add("");
            lore.add("§6➤ Cliquez pour déverrouiller");
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Formate le prix avec séparateurs
     */
    private String formatPrice(double price) {
        return String.format("%,.0f", price);
    }

    /**
     * Déverrouille un slot pour un joueur
     */
    public boolean unlockSlot(Player player, int slot) {
        double price = backpackManager.getPriceForSlot(slot);

        // Vérifier si le joueur a assez d'argent
        if (economy.getBalance(player) < price) {
            player.sendMessage(messageManager.getMessage("backpack.not-enough-money",
                    formatPrice(price - economy.getBalance(player))));
            return false;
        }

        // Retirer l'argent
        economy.withdrawPlayer(player, price);

        // Déverrouiller le slot
        if (!backpackManager.unlockSlot(player, slot)) {
            // Revenir l'argent si le déverrouillage échoue
            economy.depositPlayer(player, price);
            player.sendMessage(messageManager.getMessage("backpack.unlock-failed"));
            return false;
        }

        player.sendMessage(messageManager.getMessage("backpack.slot-unlocked",
                (slot + 1), formatPrice(price)));

        return true;
    }

    /**
     * Sauvegarde l'inventaire du backpack
     */
    public void saveBackpackInventory(Player player) {
        String title = player.getOpenInventory().getTitle();

        if (!title.contains("Backpack")) {
            return;
        }

        Inventory inventory = player.getOpenInventory().getTopInventory();

        Map<Integer, ItemStack> items = new java.util.HashMap<>();
        Set<Integer> unlockedSlots = backpackManager.getUnlockedSlots(player.getUniqueId());

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            // Sauvegarder seulement si c'est un slot déverrouillé et qu'il y a un item
            // valide
            if (unlockedSlots.contains(i) && item != null && !item.getType().isAir()) {
                items.put(i, item);
            }
        }

        inventoryManager.saveBackpackInventory(player.getUniqueId(), items);
    }
}
