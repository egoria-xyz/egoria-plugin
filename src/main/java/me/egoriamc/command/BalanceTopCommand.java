package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.BalanceTopGuiManager;
import me.egoriamc.manager.BalanceTopPageManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Commande /baltop - Affiche le classement des joueurs par argent
 */
public class BalanceTopCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;

    public BalanceTopCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Vérifier que c'est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("errors.player-only"));
            return true;
        }

        Player player = (Player) sender;

        // Récupérer la page demandée (par défaut la page sauvegardée du joueur)
        int page = BalanceTopPageManager.getPlayerPage(player.getUniqueId());
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
        BalanceTopPageManager.setPlayerPage(player.getUniqueId(), page);
        Inventory inventory = BalanceTopGuiManager.createBalanceTopInventory(page);
        player.openInventory(inventory);

        return true;
    }
}
