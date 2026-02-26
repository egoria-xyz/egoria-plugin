package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /live - Permet aux Streamers de faire des annonces de live
 */
public class LiveCommand implements CommandExecutor {

    private final MessageManager messageManager;

    public LiveCommand(EgoriaMC plugin) {
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[EgoriaMC] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        // Vérifier la permission "Streamer"
        if (!player.hasPermission("egoriamc.live.use")) {
            player.sendMessage(messageManager
                    .translateColors("&c❌ Seuls les Streamers peuvent utiliser cette commande."));
            return true;
        }

        // Vérifier que l'URL est fournie
        if (args.length == 0) {
            player.sendMessage(messageManager.translateColors("&c❌ Utilisation: &f/live <URL>"));
            player.sendMessage(messageManager.translateColors("&cExemple: &f/live https://twitch.tv/VotreChaine"));
            return true;
        }

        // Récupérer l'URL
        String liveUrl = args[0];

        // Valider que c'est une URL (basique)
        if (!liveUrl.startsWith("http://") && !liveUrl.startsWith("https://")) {
            player.sendMessage(messageManager.translateColors("&c❌ L'URL doit commencer par http:// ou https://"));
            return true;
        }

        // Créer et envoyer l'annonce de live
        String streamerName = player.getName();
        String liveAnnouncement = messageManager.translateColors(
                "\n" +
                        "&6&m" + "=".repeat(50) + "\n" +
                        "&6&l🔴 LIVE EN COURS 🔴\n" +
                        "&f" + streamerName + " &eest en direct !\n" +
                        "&bURL: &f" + liveUrl + "\n" +
                        "&6&m" + "=".repeat(50) + "\n");

        // Diffuser l'annonce à tous les joueurs
        Bukkit.broadcastMessage(liveAnnouncement);

        // Feedback au joueur
        player.sendMessage(messageManager.translateColors("&a✓ Annonce de live envoyée !"));

        // Son pour tous les joueurs
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }

        return true;
    }
}
