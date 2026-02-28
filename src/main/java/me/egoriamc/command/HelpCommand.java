package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.HelpGuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /help - Affiche l'aide dans une GUI
 */
public class HelpCommand implements CommandExecutor {

    private final HelpGuiManager helpGuiManager;

    public HelpCommand(EgoriaMC plugin) {
        this.helpGuiManager = plugin.getHelpGuiManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[EgoriaMC] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        helpGuiManager.openHelpGui(player);
        return true;
    }
}
