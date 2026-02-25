package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /help - Affiche l'aide automatiquement
 */
public class HelpCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;

    public HelpCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[EgoriaMC] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        player.sendMessage(messageManager.translateColors("&e╔════════════════════════════════╗"));
        player.sendMessage(messageManager.translateColors("&e║   &a🏠 EgoriaMC - Aide du Plugin&e  ║"));
        player.sendMessage(messageManager.translateColors("&e╚════════════════════════════════╝"));
        player.sendMessage("");

        // Section Homes
        if (player.hasPermission("egoriamc.home.use")) {
            player.sendMessage(messageManager.translateColors("&a📍 HOMES - Gestion personnelle"));
            player.sendMessage(messageManager.translateColors("&7  /home &b- Téléporter au premier home"));
            player.sendMessage(
                    messageManager.translateColors(
                            "&7  /home list &b- Ouvrir l'interface de vos homes (alias: &f/home ls&b)"));
            player.sendMessage(messageManager.translateColors("&7  /home set <nom> &b- Créer un home"));
            player.sendMessage(messageManager.translateColors("&7  /home tp <nom> &b- Se téléporter à un home"));
            player.sendMessage(messageManager
                    .translateColors("&7  /home delete <nom> &b- Supprimer un home (alias: &f/home del&b)"));
            player.sendMessage("");
        }

        // Section Furnace
        if (player.hasPermission("egoriamc.furnace.use")) {
            player.sendMessage(messageManager.translateColors("&a🔥 FURNACE - Cuisson rapide"));
            player.sendMessage(messageManager.translateColors("&7  /furnace &b- Cuit automatiquement l'item en main"));
            player.sendMessage("");
        }

        // Section Warps
        if (player.hasPermission("egoriamc.warp.use")) {
            player.sendMessage(messageManager.translateColors("&a🌍 WARPS - Points de téléportation publics"));
            player.sendMessage(messageManager.translateColors("&7  /warp <nom> &b- Se téléporter à un warp"));
            player.sendMessage(messageManager.translateColors("&7  /warp list &b- Afficher tous les warps"));
            player.sendMessage(messageManager.translateColors("&7  /warp info <nom> &b- Infos sur un warp"));
            player.sendMessage("");
        }

        // Section Staff
        if (player.hasPermission("egoriamc.home.staff")) {
            player.sendMessage(messageManager.translateColors("&d👮 STAFF - Commandes de modération"));
            // Section Homes Staff
            player.sendMessage(
                    messageManager.translateColors("&7  /home staffhomes <joueur> &b- Voir les homes d'un joueur"));
            player.sendMessage(messageManager.translateColors("&7                           &8(alias: &f/home sh&8)"));
            // Section Warps Admin
            player.sendMessage(messageManager.translateColors("&c  /warp set <nom> &b- Créer un warp (Admin)"));
            player.sendMessage(
                    messageManager.translateColors("&c  /warp delete <nom> &b- Supprimer un warp (Admin)"));
            player.sendMessage("");
        }

        // Section Admin
        if (player.hasPermission("egoriamc.home.admin") || player.hasPermission("egoriamc.warp.admin")
                || player.hasPermission("egoriamc.announce")) {
            player.sendMessage(messageManager.translateColors("&c👨‍💼 ADMIN - Commandes d'administration"));
            if (player.hasPermission("egoriamc.home.admin")) {
                player.sendMessage(messageManager.translateColors("&7  /home set <nom> &b- Créer un home personnel"));
            }
            if (player.hasPermission("egoriamc.warp.admin")) {
                player.sendMessage(messageManager.translateColors("&7  /warp set <nom> &b- Créer un warp public"));
                player.sendMessage(messageManager.translateColors("&7  /warp delete <nom> &b- Supprimer un warp"));
            }
            if (player.hasPermission("egoriamc.announce")) {
                player.sendMessage(
                        messageManager.translateColors("&7  /annonce <message> &b- Envoyer une annonce (Staff)"));
            }
            player.sendMessage("");
        }

        player.sendMessage(
                messageManager.translateColors("&e└─ Besoin d'aide? Tapez &b/help &epour réviser les commandes"));

        return true;
    }
}
