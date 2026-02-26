package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /craft - Ouvre une table de craft
 * Seuls les joueurs avec le grade 'Gardien' peuvent utiliser cette commande
 */
public class CraftCommand implements CommandExecutor {

    private final MessageManager messageManager;

    public CraftCommand(EgoriaMC plugin) {
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[EgoriaMC] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        // Vérifier la permission "Gardien"
        if (!player.hasPermission("egoriamc.craft.use")) {
            player.sendMessage(messageManager
                    .translateColors("&c❌ Seuls les joueurs avec le grade 'Gardien' peuvent utiliser cette commande."));
            return true;
        }

        // Ouvrir la table de craft
        player.openWorkbench(null, true);
        player.sendMessage(messageManager.translateColors("&a✓ Table de craft ouverte !"));

        return true;
    }
}
