package me.egoriamc.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Commande /plugins - Affiche un GUI avec la liste des plugins
 */
public class PluginsCommand implements CommandExecutor {

    private static final int INVENTORY_SIZE = 27;
    private static final String EGORIAMC_PLUGIN_NAME = "EgoraIMC";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[EgoraIMC] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (!player.hasPermission("egoriamc.plugins")) {
            player.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }

        // Créer l'inventaire
        Inventory inventory = createPluginsInventory(player);
        player.openInventory(inventory);

        return true;
    }

    /**
     * Crée l'inventaire avec la liste des plugins
     */
    private Inventory createPluginsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, 
            "§ePlugins du Serveur");

        // Récupérer tous les plugins
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        // Ajouter un item pour chaque plugin
        int slot = 0;
        for (Plugin plugin : plugins) {
            if (slot >= INVENTORY_SIZE) break;

            ItemStack item = createPluginItem(plugin);
            inventory.setItem(slot, item);
            slot++;
        }

        return inventory;
    }

    /**
     * Crée un item (livre) pour un plugin
     */
    private ItemStack createPluginItem(Plugin plugin) {
        // Utiliser un livre comme item
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        // Nom du plugin
        meta.setDisplayName("§e" + plugin.getName());

        // Lore avec version
        List<String> lore = new ArrayList<>();
        String version = "1.21.11-" + plugin.getDescription().getVersion();
        lore.add("§7Version: §f" + version);
        lore.add("");
        lore.add("§7Statut: §a" + (plugin.isEnabled() ? "Activé" : "Désactivé"));

        // Ajouter l'auteur si disponible
        if (plugin.getDescription().getAuthors() != null && !plugin.getDescription().getAuthors().isEmpty()) {
            lore.add("§7Auteurs: §f" + String.join(", ", plugin.getDescription().getAuthors()));
        }

        // Ajouter la description si disponible
        if (plugin.getDescription().getDescription() != null && 
            !plugin.getDescription().getDescription().isEmpty()) {
            lore.add("");
            lore.add("§7Description:");
            String description = plugin.getDescription().getDescription();
            // Limiter la description à 2 lignes
            if (description.length() > 40) {
                lore.add("§8" + description.substring(0, 40) + "...");
            } else {
                lore.add("§8" + description);
            }
        }

        meta.setLore(lore);

        // Enchanter le plugin EgoraIMC
        if (plugin.getName().equalsIgnoreCase(EGORIAMC_PLUGIN_NAME)) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            // Changer la couleur du nom pour l'identifier
            meta.setDisplayName("§6✨ " + plugin.getName() + " ✨");
            
            // Ajouter un indicateur dans la lore
            List<String> updatedLore = meta.getLore();
            updatedLore.add(0, "§6➤ Développé par le serveur");
        }

        item.setItemMeta(meta);
        return item;
    }
}
