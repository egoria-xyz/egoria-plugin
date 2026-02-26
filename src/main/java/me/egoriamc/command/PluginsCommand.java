package me.egoriamc.command;

import me.egoriamc.manager.PluginsPageManager;
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
 * Commande /plugins - Affiche un GUI avec la liste des plugins sur plusieurs
 * pages
 */
public class PluginsCommand implements CommandExecutor {

    private static final int INVENTORY_SIZE = 27;
    private static final int PLUGINS_PER_PAGE = 25; // 25 plugins + 2 boutons de navigation
    private static final String EGORIAMC_PLUGIN_NAME = "EgoriaMC";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[EgoriaMC] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (!player.hasPermission("egoriamc.plugins")) {
            player.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }

        // Récupérer la page demandée (par défaut la page sauvegardée du joueur)
        int page = PluginsPageManager.getPlayerPage(player.getUniqueId());
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1)
                    page = 1;
            } catch (NumberFormatException e) {
                player.sendMessage("§cNuméro de page invalide.");
                return true;
            }
        }

        // Sauvegarder la page et créer l'inventaire
        PluginsPageManager.setPlayerPage(player.getUniqueId(), page);
        Inventory inventory = createPluginsInventory(page);
        player.openInventory(inventory);

        return true;
    }

    /**
     * Crée l'inventaire avec la liste des plugins (méthode publique)
     */
    public static Inventory createPluginsInventory(int page) {
        // Récupérer tous les plugins
        Plugin[] allPlugins = Bukkit.getPluginManager().getPlugins();

        // Calculer le nombre de pages
        int totalPages = (int) Math.ceil((double) allPlugins.length / PLUGINS_PER_PAGE);
        if (page > totalPages)
            page = totalPages;
        if (page < 1)
            page = 1;

        // Créer le titre avec le numéro de page
        String title = "§ePlugins du Serveur (" + page + "/" + totalPages + ")";
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, title);

        // Calculer les indices de début et fin
        int startIndex = (page - 1) * PLUGINS_PER_PAGE;
        int endIndex = Math.min(startIndex + PLUGINS_PER_PAGE, allPlugins.length);

        // Ajouter un item pour chaque plugin de cette page
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot >= 25)
                break; // Garder 2 slots pour les boutons de navigation
            ItemStack item = createPluginItem(allPlugins[i]);
            inventory.setItem(slot, item);
            slot++;
        }

        // Ajouter les boutons de navigation
        if (page > 1) {
            ItemStack previousButton = createNavigationButton("§e◄ Page Précédente", page - 1);
            inventory.setItem(25, previousButton);
        }

        if (page < totalPages) {
            ItemStack nextButton = createNavigationButton("§eProchaine Page ►", page + 1);
            inventory.setItem(26, nextButton);
        }

        return inventory;
    }

    /**
     * Crée un bouton de navigation
     */
    private static ItemStack createNavigationButton(String displayName, int page) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();
        lore.add("§7Cliquez pour aller à la page " + page);
        lore.add("§7Commande: §f/egoriamc:plugins " + page);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Crée un item (livre) pour un plugin
     */
    private static ItemStack createPluginItem(Plugin plugin) {
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

        // Enchanter le plugin EgoriaMC
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
