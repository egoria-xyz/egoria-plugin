package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import me.egoriamc.manager.WarpManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /warp
 */
public class WarpCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final WarpManager warpManager;
    private final MessageManager messageManager;

    public WarpCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.warpManager = plugin.getWarpManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[EgoriaMC] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        // Pas d'argument : afficher la liste des warps
        if (args.length == 0) {
            handleList(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set" -> handleSet(player, args);
            case "delete", "del" -> handleDelete(player, args);
            case "list", "ls" -> handleList(player);
            case "info" -> handleInfo(player, args);
            default -> handleTeleport(player, subCommand);
        }

        return true;
    }

    private void handleSet(Player player, String[] args) {
        if (!player.hasPermission("egoriamc.warp.admin")) {
            player.sendMessage(messageManager.translateColors("&cVous n'avez pas la permission."));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(messageManager.translateColors("&cUtilisation: /warp set <nom>"));
            return;
        }

        String warpName = args[1];

        // Valider le nom
        if (warpName.length() > 16) {
            player.sendMessage(messageManager.translateColors("&cLe nom du warp ne peut pas dépasser 16 caractères."));
            return;
        }

        if (!warpName.matches("^[a-zA-Z0-9_-]+$")) {
            player.sendMessage(messageManager
                    .translateColors("&cNom invalide. Utilisez uniquement des lettres, chiffres, - et _."));
            return;
        }

        Location location = player.getLocation();

        if (warpManager.createWarp(warpName, location, player.getName())) {
            player.sendMessage(messageManager.getWarpSetSuccess(warpName));
            plugin.logInfo(
                    messageManager.translateColors("&a" + player.getName() + " &ea créé le warp: &b" + warpName));
        } else {
            player.sendMessage(messageManager.getWarpAlreadyExists(warpName));
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (!player.hasPermission("egoriamc.warp.admin")) {
            player.sendMessage(messageManager.translateColors("&cVous n'avez pas la permission."));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(messageManager.translateColors("&cUtilisation: /warp delete <nom>"));
            return;
        }

        String warpName = args[1];

        if (warpManager.deleteWarp(warpName)) {
            player.sendMessage(messageManager.getWarpDeleteSuccess(warpName));
            plugin.logInfo(
                    messageManager.translateColors("&a" + player.getName() + " &ea supprimé le warp: &b" + warpName));
        } else {
            player.sendMessage(messageManager.getWarpNotFound());
        }
    }

    private void handleList(Player player) {
        // Ouvrir la GUI de la liste des warps
        plugin.getWarpListGuiManager().openWarpListGui(player, 1);
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageManager.translateColors("&cUtilisation: /warp info <nom>"));
            return;
        }

        String warpName = args[1];
        Location warp = warpManager.getWarp(warpName);

        if (warp == null) {
            player.sendMessage(messageManager.getWarpNotFound());
            return;
        }

        String creator = warpManager.getWarpCreator(warpName);
        String world = warp.getWorld() != null ? warp.getWorld().getName() : "Inconnue";

        player.sendMessage(messageManager.translateColors("&e=== Info Warp: " + warpName + " ==="));
        player.sendMessage(messageManager.translateColors("&aLieu: &e" + world));
        player.sendMessage(messageManager.translateColors("&aCoordonnées: &eX:" +
                String.format("%.1f", warp.getX()) + " Y:" + String.format("%.1f", warp.getY()) +
                " Z:" + String.format("%.1f", warp.getZ())));
        player.sendMessage(messageManager.translateColors("&aCréateur: &e" + creator));
    }

    private void handleTeleport(Player player, String warpName) {
        if (!player.hasPermission("egoriamc.warp.use")) {
            player.sendMessage(messageManager.translateColors("&cVous n'avez pas la permission."));
            return;
        }

        Location warp = warpManager.getWarp(warpName);

        if (warp == null) {
            player.sendMessage(messageManager.getWarpNotFound());
            return;
        }

        player.teleport(warp);
        player.sendMessage(messageManager.getWarpTpSuccess(warpName));
        plugin.logInfo(
                messageManager.translateColors("&a" + player.getName() + " &es'est téléporté au warp: &b" + warpName));
    }
}
