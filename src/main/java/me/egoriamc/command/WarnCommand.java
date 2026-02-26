package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import me.egoriamc.manager.WarnManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /warn <pseudo> <raison> - Enregistre un avertissement en BDD.
 */
public class WarnCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;
    private final WarnManager warnManager;

    public WarnCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.warnManager = plugin.getWarnManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("egoriamc.warn")) {
            sender.sendMessage(messageManager.translateColors("&cVous n'avez pas la permission d'utiliser cette commande."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(messageManager.translateColors("&eUsage: &f/warn <pseudo> <raison>"));
            return true;
        }

        String username = args[0];
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        String warnerName = sender.getName();
        String serverName = Bukkit.getServer().getName();

        if (!plugin.getDatabaseManager().isConfigured()) {
            sender.sendMessage(messageManager.translateColors("&cLa base de données n'est pas configurée (database.yml)."));
            return true;
        }

        warnManager.addWarn(username, reason, warnerName, serverName);

        sender.sendMessage(messageManager.translateColors("&aAvertissement enregistré pour &f" + username + " &a: &f" + reason));

        Player target = Bukkit.getPlayerExact(username);
        if (target != null && target.isOnline()) {
            target.sendMessage(messageManager.translateColors("&cVous avez reçu un avertissement : &f" + reason));
        }

        return true;
    }
}
