package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de la GUI d'aide
 */
public class HelpGuiManager {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;

    public HelpGuiManager(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    /**
     * Ouvre la GUI d'aide principal
     */
    public void openHelpGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, messageManager.translateColors("&e🏠 Aide du Plugin"));

        // Homes
        if (player.hasPermission("egoriamc.home.use")) {
            gui.setItem(10, createItem(Material.GRASS_BLOCK, "&a📍 HOMES", Arrays.asList(
                    "&7Gestion personnelle des homes",
                    "&7Sauvegardez vos localisations préférées",
                    " ",
                    "&e/home &7- Accueil principal",
                    "&e/home list &7- Voir vos homes",
                    "&e/home set <nom> &7- Créer un home",
                    "&e/home tp <nom> &7- Se téléporter",
                    "&e/home delete <nom> &7- Supprimer")));
        }

        // Warps
        if (player.hasPermission("egoriamc.warp.use")) {
            gui.setItem(11, createItem(Material.END_ROD, "&a🌍 WARPS", Arrays.asList(
                    "&7Points de téléportation publics",
                    "&7Partagés par les administrateurs",
                    " ",
                    "&e/warp <nom> &7- Se téléporter",
                    "&e/warp list &7- Voir les warps",
                    "&e/warp info <nom> &7- Infos détaillées")));
        }

        // Backpack
        if (player.hasPermission("egoriamc.backpack.use")) {
            gui.setItem(12, createItem(Material.CHEST, "&a🎒 BACKPACK", Arrays.asList(
                    "&7Stockage personnel illimité",
                    "&7Accessible de partout",
                    " ",
                    "&e/backpack &7- Ouvrir votre backpack",
                    "&7Les slots supplémentaires se",
                    "&7déverrouillent avec de l'argent")));
        }

        // Furnace
        if (player.hasPermission("egoriamc.furnace.use")) {
            gui.setItem(13, createItem(Material.FURNACE, "&a🔥 FURNACE", Arrays.asList(
                    "&7Cuisson automatique",
                    "&7Cuit l'item en main instantanément",
                    " ",
                    "&e/furnace &7- Cuire l'item en main",
                    "&7Grade Gardien minimum requis")));
        }

        // Craft
        if (player.hasPermission("egoriamc.craft.use")) {
            gui.setItem(14, createItem(Material.CRAFTING_TABLE, "&a🛠️ CRAFT", Arrays.asList(
                    "&7Table de craft portable",
                    "&7Ouvre une interface de craft",
                    " ",
                    "&e/craft &7- Ouvrir la table de craft",
                    "&7Grade Gardien minimum requis")));
        }

        // Balance
        if (player.hasPermission("egoriamc.balance.use")) {
            gui.setItem(15, createItem(Material.GOLD_INGOT, "&e💰 ARGENT", Arrays.asList(
                    "&7Système d'économie Vault",
                    "&7Gestion de votre compte bancaire",
                    " ",
                    "&e/balance &7- Voir votre solde",
                    "&e/money &7- Alias de balance",
                    "&7Utilisez l'argent pour débloquer",
                    "&7des slots du backpack")));
        }

        // Live
        if (player.hasPermission("egoriamc.live.use")) {
            gui.setItem(16, createItem(Material.REDSTONE_LAMP, "&a📺 LIVE", Arrays.asList(
                    "&7Annonce de votre live Twitch",
                    "&7Visible par tous les joueurs",
                    " ",
                    "&e/live <url> &7- Annoncer un live",
                    "&7Grade Streamer minimum requis")));
        }

        // Staff - Section séparée
        if (player.hasPermission("egoriamc.home.staff") || player.hasPermission("egoriamc.announce")) {
            gui.setItem(19, createItem(Material.REDSTONE, "&d👮 STAFF", Arrays.asList(
                    "&7Commandes de modération",
                    "&7Outils pour les administrateurs",
                    " ",
                    "&eClic pour voir les détails",
                    "&7Accès restreint")));
        }

        // Admin - Section séparée
        if (player.hasPermission("egoriamc.addmoney") || player.hasPermission("egoriamc.warn")) {
            gui.setItem(20, createItem(Material.DRAGON_EGG, "&c👨‍💼 ADMIN", Arrays.asList(
                    "&7Commandes d'administration",
                    "&7Gestion du serveur",
                    " ",
                    "&eClic pour voir les détails",
                    "&7Accès restreint")));
        }

        // Fermeture
        gui.setItem(26, createItem(Material.BARRIER, "&c✕ Fermer", Arrays.asList(
                "&7Clic droit pour fermer")));

        player.openInventory(gui);
    }

    /**
     * Ouvre la GUI des commandes Staff
     */
    public void openStaffGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, messageManager.translateColors("&d👮 Commandes Staff"));

        int slot = 10;

        if (player.hasPermission("egoriamc.home.staff")) {
            gui.setItem(slot++, createItem(Material.GRASS_BLOCK, "&a📍 HOMES STAFF", Arrays.asList(
                    "&7Voir les homes des joueurs",
                    " ",
                    "&e/home staffhomes <joueur>",
                    "&7Alias: &f/home sh")));
        }

        if (player.hasPermission("egoriamc.announce")) {
            gui.setItem(slot++, createItem(Material.BELL, "&a📢 ANNONCES", Arrays.asList(
                    "&7Envoyer une annonce globale",
                    "&7Visible par tous les joueurs",
                    " ",
                    "&e/annonce <message>",
                    "&7Alias: &f/announce&7, &f/broadcast")));
        }

        if (player.hasPermission("egoriamc.warn")) {
            gui.setItem(slot++, createItem(Material.REDSTONE, "&c⚠️ WARNS", Arrays.asList(
                    "&7Système d'avertissements",
                    "&7Enregistré en base de données",
                    " ",
                    "&e/warn <joueur> <raison>",
                    "&7Limite: 3 warns = kick")));
        }

        if (player.hasPermission("egoriamc.reload")) {
            gui.setItem(slot++, createItem(Material.WRITABLE_BOOK, "&e🔄 RELOAD", Arrays.asList(
                    "&7Recharge la configuration",
                    "&7Recharge tous les fichiers .yml",
                    " ",
                    "&e/reload",
                    "&7Redémarrage du plugin non requis")));
        }

        // Retour
        gui.setItem(26, createItem(Material.ARROW, "&b← Retour", Arrays.asList(
                "&7Retour à l'aide principale")));

        player.openInventory(gui);
    }

    /**
     * Ouvre la GUI des commandes Admin
     */
    public void openAdminGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, messageManager.translateColors("&c👨‍💼 Commandes Admin"));

        int slot = 10;

        if (player.hasPermission("egoriamc.addmoney")) {
            gui.setItem(slot++, createItem(Material.GOLD_NUGGET, "&a➕ AJOUTER ARGENT", Arrays.asList(
                    "&7Ajoute de l'argent à un joueur",
                    "&7Système Vault intégré",
                    " ",
                    "&e/addmoney <joueur> <montant>")));
        }

        if (player.hasPermission("egoriamc.removemoney")) {
            gui.setItem(slot++, createItem(Material.COPPER_INGOT, "&a➖ RETIRER ARGENT", Arrays.asList(
                    "&7Retire de l'argent à un joueur",
                    "&7Système Vault intégré",
                    " ",
                    "&e/removemoney <joueur> <montant>")));
        }

        // Retour
        gui.setItem(26, createItem(Material.ARROW, "&b← Retour", Arrays.asList(
                "&7Retour à l'aide principale")));

        player.openInventory(gui);
    }

    /**
     * Crée un item avec lore
     */
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(messageManager.translateColors(name));

            // Traduire chaque ligne de la lore
            List<String> translatedLore = new ArrayList<>();
            for (String line : lore) {
                translatedLore.add(messageManager.translateColors(line));
            }
            meta.setLore(translatedLore);

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }

        return item;
    }
}
