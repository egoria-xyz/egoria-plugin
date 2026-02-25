package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /reload - Permet de recharger la configuration du plugin
 */
public class ReloadCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;

    public ReloadCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vérifier les permissions
        if (!sender.isOp() && !(sender instanceof Player player && player.hasPermission("egoriamc.reload"))) {
            sender.sendMessage(
                    messageManager.translateColors("&c❌ Vous n'avez pas la permission d'utiliser cette commande."));
            return true;
        }

        try {
            // Recharger la configuration principale
            plugin.getConfigManager().reload();
            sender.sendMessage(messageManager.translateColors("&a✓ Configuration principale rechargée"));

            // Recharger les messages automatiques
            plugin.getAutoMessageManager().reloadConfig();
            sender.sendMessage(messageManager.translateColors("&a✓ Messages automatiques rechargés"));

            sender.sendMessage(
                    messageManager.translateColors("&a✓ Toutes les configurations ont été rechargées avec succès !"));
        } catch (Exception e) {
            sender.sendMessage(messageManager.translateColors("&c❌ Erreur lors du rechargement des configurations"));
            plugin.logError("Erreur lors du rechargement des configurations");
            e.printStackTrace();
        }

        return true;
    }
}
