package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.EconomyManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /balance - Affiche le solde du joueur
 */
public class BalanceCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;
    private final EconomyManager economyManager;

    public BalanceCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Vérifier que c'est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("errors.player-only"));
            return true;
        }

        Player player = (Player) sender;

        // Vérifier que l'économie est activée
        if (!economyManager.isEnabled()) {
            player.sendMessage(messageManager.getMessage("errors.economy-disabled"));
            return true;
        }

        // Si un joueur est spécifié et le joueur a la permission
        if (args.length > 0 && player.hasPermission("egoriamc.balance.others")) {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(messageManager.getMessage("errors.player-not-found"));
                return true;
            }
            displayBalance(player, target, false);
        } else if (args.length > 0) {
            player.sendMessage(messageManager.getMessage("errors.no-permission"));
            return true;
        } else {
            // Afficher son propre solde
            displayBalance(player, player, true);
        }

        return true;
    }

    /**
     * Affiche le solde d'un joueur
     */
    private void displayBalance(Player sender, Player target, boolean isSelf) {
        double balance = economyManager.getBalance(target);
        String formattedBalance = economyManager.formatBalance(balance);

        if (isSelf) {
            String message = "§6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "§e💰 Votre solde: §f" + formattedBalance + "\n" +
                    "§6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
            sender.sendMessage(message);
        } else {
            String message = "§6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "§e💰 Solde de §f" + target.getName() + "§e: §f" + formattedBalance + "\n" +
                    "§6━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
            sender.sendMessage(message);
        }
    }
}
