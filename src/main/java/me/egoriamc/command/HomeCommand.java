package me.egoriamc.command;

import me.egoriamc.EgoraIMC;
import me.egoriamc.manager.HomeManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Commande /home
 */
public class HomeCommand implements CommandExecutor {

    private final EgoraIMC plugin;
    private final HomeManager homeManager;
    private final MessageManager messageManager;

    public HomeCommand(EgoraIMC plugin) {
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
            case "list", "ls" -> handleList(player);
            case "tp" -> handleTeleport(player, args);
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
            player.sendMessage(messageManager.translateColors("₾ → &7Le nom du home ne peut pas dépasser 16 caractères."));
            return;
        }

        if (!homeName.matches("^[a-zA-Z0-9_-]+$")) {
            player.sendMessage(messageManager.translateColors("₾ → &7Nom invalide. Utilisez uniquement des lettres, chiffres, - et _."));
            return;
        }

        Location location = player.getLocation();

        if (homeManager.createHome(player, homeName, location)) {
            player.sendMessage(messageManager.getHomeSetSuccess(homeName));
            plugin.logInfo(messageManager.translateColors("₾ → &a" + player.getName() + " &ea créé un home: &b" + homeName));
        } else {
            player.sendMessage(messageManager.getHomeLimitReached(
                plugin.getConfigManager().getMaxHomes()
            ));
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
            plugin.logInfo(messageManager.translateColors("₾ → &a" + player.getName() + " &ea supprimé le home: &b" + homeName));
        } else {
            player.sendMessage(messageManager.getHomeNotFound());
        }
    }

    private void handleList(Player player) {
        Map<String, Location> homes = homeManager.getPlayerHomes(player.getUniqueId());

        if (homes.isEmpty()) {
            player.sendMessage(messageManager.getHomeListEmpty());
            return;
        }

        player.sendMessage(messageManager.getHomeListHeader());
        for (Map.Entry<String, Location> entry : homes.entrySet()) {
            String homeName = entry.getKey();
            Location loc = entry.getValue();
            String world = loc.getWorld() != null ? loc.getWorld().getName() : "Inconnue";
            player.sendMessage(messageManager.getHomeListItem(homeName, world));
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

    private void teleportHome(Player player, String homeName) {
        Location home = homeManager.getHome(player.getUniqueId(), homeName);

        if (home == null) {
            player.sendMessage(messageManager.getHomeNotFound());
            return;
        }

        player.teleport(home);
        player.sendMessage(messageManager.getHomeTpSuccess(homeName));
        plugin.logInfo(messageManager.translateColors("₾ → &a" + player.getName() + " &es'est téléporté au home: &b" + homeName));
    }

    private void sendUsage(Player player) {
        player.sendMessage(messageManager.translateColors("&e=== Commandes Homes ==="));
        player.sendMessage(messageManager.translateColors("&a/home &7- Téléporter au premier home"));
        player.sendMessage(messageManager.translateColors("&a/home set <nom> &7- Créer un home"));
        player.sendMessage(messageManager.translateColors("&a/home tp <nom> &7- Téléporter à un home"));
        player.sendMessage(messageManager.translateColors("&a/home delete <nom> &7- Supprimer un home"));
        player.sendMessage(messageManager.translateColors("&a/home list &7- Lister vos homes"));
    }
}
