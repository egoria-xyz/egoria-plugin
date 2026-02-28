package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.EconomyManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /addmoney - Ajoute de l'argent à un joueur (OP)
 */
public class AddMoneyCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;
    private final EconomyManager economyManager;

    public AddMoneyCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Vérifier les permissions
        if (!sender.hasPermission("egoriamc.addmoney")) {
            sender.sendMessage(messageManager.getMessage("errors.no-permission"));
            return true;
        }

        // Vérifier que l'économie est activée
        if (!economyManager.isEnabled()) {
            sender.sendMessage(messageManager.getMessage("errors.economy-disabled"));
            return true;
        }

        // Vérifier les arguments
        if (args.length < 2) {
            sender.sendMessage("§cUtilisation: /addmoney <joueur> <montant>");
            return true;
        }

        // Récupérer le joueur
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(messageManager.getMessage("errors.player-not-found"));
            return true;
        }

        // Vérifier que le montant est valide
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount < 0) {
                sender.sendMessage("§cLe montant doit être positif.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cLe montant doit être un nombre valide.");
            return true;
        }

        // Ajouter l'argent
        economyManager.addBalance(target, amount);
        String formattedAmount = economyManager.formatBalance(amount);

        // Messages
        sender.sendMessage("§a✓ Vous avez ajouté " + formattedAmount + " à §e" + target.getName());
        target.sendMessage("§6✓ Vous avez reçu " + formattedAmount + "§6 de la part d'un administrateur");

        return true;
    }
}
