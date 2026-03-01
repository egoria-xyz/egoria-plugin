package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import me.egoriamc.manager.RepairInventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Commande /repair - Ouvre l'interface de réparation
 * Disponible pour les Gardiens, Streamers et Staff
 */
public class RepairCommand implements CommandExecutor {

    private final MessageManager messageManager;
    private final RepairInventoryManager inventoryManager;

    public RepairCommand(EgoriaMC plugin) {
        this.messageManager = plugin.getMessageManager();
        this.inventoryManager = new RepairInventoryManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager
                    .translateColors("&c[EgoriaMC] Cette commande ne peut être utilisée que par un joueur."));
            return true;
        }

        Player player = (Player) sender;

        // Vérifier la permission (Gardien, Streamer ou Staff)
        if (!hasRepairPermission(player)) {
            player.sendMessage(messageManager.translateColors(
                    "&c❌ Vous n'avez pas la permission d'utiliser cette commande. " +
                            "Seuls les Gardiens, Streamers et Staff peuvent réparer les équipements."));
            return true;
        }

        // Ouvrir l'interface de réparation
        Inventory repairInventory = inventoryManager.createRepairInventory(player);
        player.openInventory(repairInventory);

        return true;
    }

    /**
     * Vérifie si le joueur a la permission de réparer
     */
    private boolean hasRepairPermission(Player player) {
        return player.hasPermission("egoriamc.repair.gardien") ||
                player.hasPermission("egoriamc.repair.streamer") ||
                player.hasPermission("egoriamc.repair.staff");
    }
}
