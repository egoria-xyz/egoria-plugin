package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.HomeManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Commande /home
 */
public class HomeCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final HomeManager homeManager;
    private final MessageManager messageManager;

    public HomeCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.homeManager = plugin.getHomeManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("₾ → &7Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (!player.hasPermission("egoriamc.home.use")) {
            player.sendMessage(messageManager.translateColors("₾ → &7Vous n'avez pas la permission."));
            return true;
        }

        // Pas d'argument : téléport au premier home
        if (args.length == 0) {
            Map<String, Location> homes = homeManager.getPlayerHomes(player.getUniqueId());
            if (homes.isEmpty()) {
                player.sendMessage(messageManager.getHomeListEmpty());
                return true;
            }

            String firstName = homes.keySet().iterator().next();
            teleportHome(player, firstName);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set" -> handleSet(player, args);
            case "delete", "del" -> handleDelete(player, args);
            case "list", "ls" -> handleGui(player);
            case "tp" -> handleTeleport(player, args);
            case "staffhomes", "sh" -> handleStaffHomes(player, args);
            default -> sendUsage(player);
        }

        return true;
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageManager.translateColors("₾ → &7Utilisation: /home set <nom>"));
            return;
        }

        String homeName = args[1];

        // Valider le nom
        if (homeName.length() > 16) {
            player.sendMessage(
                    messageManager.translateColors("₾ → &7Le nom du home ne peut pas dépasser 16 caractères."));
            return;
        }

        if (!homeName.matches("^[a-zA-Z0-9_-]+$")) {
            player.sendMessage(messageManager
                    .translateColors("₾ → &7Nom invalide. Utilisez uniquement des lettres, chiffres, - et _."));
            return;
        }

        Location location = player.getLocation();

        if (homeManager.createHome(player, homeName, location)) {
            player.sendMessage(messageManager.getHomeSetSuccess(homeName));
            plugin.logInfo(
                    messageManager.translateColors("₾ → &a" + player.getName() + " &ea créé un home: &b" + homeName));
        } else {
            player.sendMessage(messageManager.getHomeLimitReached(
                    plugin.getConfigManager().getMaxHomes()));
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageManager.translateColors("₾ → &7Utilisation: /home delete <nom>"));
            return;
        }

        String homeName = args[1];

        if (homeManager.deleteHome(player.getUniqueId(), homeName)) {
            player.sendMessage(messageManager.getHomeDeleteSuccess(homeName));
            plugin.logInfo(messageManager
                    .translateColors("₾ → &a" + player.getName() + " &ea supprimé le home: &b" + homeName));
        } else {
            player.sendMessage(messageManager.getHomeNotFound());
        }
    }

    private void handleTeleport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageManager.translateColors("₾ → &7Utilisation: /home tp <nom>"));
            return;
        }

        String homeName = args[1];
        teleportHome(player, homeName);
    }

    private void handleGui(Player player) {
        Map<String, Location> homes = homeManager.getPlayerHomes(player.getUniqueId());

        if (homes.isEmpty()) {
            player.sendMessage(messageManager.getHomeListEmpty());
            return;
        }

        openHomesGui(player, player.getUniqueId(), player.getName());
    }

    private void handleStaffHomes(Player player, String[] args) {
        if (!player.hasPermission("egoriamc.home.staff")) {
            player.sendMessage(messageManager.translateColors("₾ → &7Vous n'avez pas la permission."));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(messageManager.translateColors("₾ → &7Utilisation: /home staffhomes <joueur>"));
            return;
        }

        String targetName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        if (targetPlayer == null) {
            player.sendMessage(messageManager.translateColors("₾ → &7Joueur non trouvé."));
            return;
        }

        UUID targetUuid = targetPlayer.getUniqueId();
        Map<String, Location> homes = homeManager.getPlayerHomes(targetUuid);

        if (homes.isEmpty()) {
            player.sendMessage(messageManager.translateColors("₾ → &7Ce joueur n'a pas de homes."));
            return;
        }

        openHomesGui(player, targetUuid, targetName);
    }

    /**
     * Ouvre l'interface GUI des homes
     */
    private void openHomesGui(Player player, UUID targetUuid, String targetName) {
        Map<String, Location> homes = homeManager.getPlayerHomes(targetUuid);

        if (homes.isEmpty()) {
            player.sendMessage(messageManager.getHomeListEmpty());
            return;
        }

        int inventorySize = Math.min(54, (int) (Math.ceil(homes.size() / 9.0) * 9));
        Inventory inventory = Bukkit.createInventory(null, inventorySize,
                messageManager.translateColors("&e" + targetName + " - Homes"));

        int slot = 0;
        for (Map.Entry<String, Location> entry : homes.entrySet()) {
            if (slot >= inventorySize)
                break;

            ItemStack homeItem = createHomeItem(entry.getKey(), entry.getValue());
            inventory.setItem(slot, homeItem);
            slot++;
        }

        player.openInventory(inventory);
    }

    /**
     * Crée un item pour un home
     */
    private ItemStack createHomeItem(String homeName, Location location) {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(messageManager.translateColors("&e" + homeName));

        List<String> lore = new ArrayList<>();
        String world = location.getWorld() != null ? location.getWorld().getName() : "Inconnue";
        lore.add(messageManager.translateColors("&7Monde: &f" + world));
        lore.add(messageManager.translateColors("&7Coordinates: &fX: " + location.getBlockX() +
                " Y: " + location.getBlockY() + " Z: " + location.getBlockZ()));
        lore.add("");
        lore.add(messageManager.translateColors("&a→ Cliquez pour vous téléporter"));

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private void teleportHome(Player player, String homeName) {
        Location home = homeManager.getHome(player.getUniqueId(), homeName);

        if (home == null) {
            player.sendMessage(messageManager.getHomeNotFound());
            return;
        }

        player.teleport(home);
        player.sendMessage(messageManager.getHomeTpSuccess(homeName));
        plugin.logInfo(messageManager
                .translateColors("₾ → &a" + player.getName() + " &es'est téléporté au home: &b" + homeName));
    }

    private void sendUsage(Player player) {
        player.sendMessage(messageManager.translateColors("&e=== Commandes Homes ==="));
        player.sendMessage(messageManager.translateColors("&a/home &7- Téléporter au premier home"));
        player.sendMessage(messageManager.translateColors("&a/home gui &7- Ouvrir l'interface GUI"));
        player.sendMessage(messageManager.translateColors("&a/home set <nom> &7- Créer un home"));
        player.sendMessage(messageManager.translateColors("&a/home tp <nom> &7- Téléporter à un home"));
        player.sendMessage(messageManager.translateColors("&a/home delete <nom> &7- Supprimer un home"));
        player.sendMessage(messageManager.translateColors("&a/home list &7- Lister vos homes"));
        if (player.hasPermission("egoriamc.home.staff")) {
            player.sendMessage(
                    messageManager.translateColors("&a/home staffhomes <joueur> &7- Voir les homes d'un joueur"));
        }
    }
}
