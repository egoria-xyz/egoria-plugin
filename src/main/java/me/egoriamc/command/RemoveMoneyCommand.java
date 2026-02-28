package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.EconomyManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /removemoney - Retire de l'argent à un joueur (OP)
 */
public class RemoveMoneyCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;
    private final EconomyManager economyManager;

    public RemoveMoneyCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Vérifier les permissions
        if (!sender.hasPermission("egoriamc.removemoney")) {
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
            sender.sendMessage("§cUtilisation: /removemoney <joueur> <montant>");
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

        // Vérifier que le joueur a suffisamment d'argent
        double balance = economyManager.getBalance(target);
        if (balance < amount) {
            sender.sendMessage("§c" + target.getName() + " n'a que " + economyManager.formatBalance(balance));
            return true;
        }

        // Retirer l'argent
        economyManager.removeBalance(target, amount);
        String formattedAmount = economyManager.formatBalance(amount);

        // Messages
        sender.sendMessage("§a✓ Vous avez retiré " + formattedAmount + " à §e" + target.getName());
        target.sendMessage("§6✗ Un administrateur vous a retiré " + formattedAmount);

        return true;
    }
}
