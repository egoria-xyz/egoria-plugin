package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import me.egoriamc.util.EmojiUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Commande /annonce - Permet aux staff de faire des annonces dans le chat
 */
public class AnnounceCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;

    public AnnounceCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vérifier si le sender est OP
        if (!sender.isOp()) {
            sender.sendMessage(messageManager.getMessage("announce.no-permission"));
            return true;
        }

        // Vérifier les arguments
        if (args.length == 0) {
            sender.sendMessage(messageManager.getMessage("announce.usage"));
            return true;
        }

        // Récupérer la configuration
        FileConfiguration config = plugin.getConfigManager().getConfig();

        // Construire le message
        String message = String.join(" ", args);

        // Remplacer les emojis :emoji:
        message = EmojiUtil.replaceEmojis(message);

        // Récupérer le format depuis la config
        String prefix = config.getString("announce.prefix", "&c&l[ANNONCE]");
        String format = config.getString("announce.format", "{prefix} &r{message}");
        boolean enableSound = config.getBoolean("announce.sound.enabled", true);
        String soundName = config.getString("announce.sound.type", "ENTITY_ENDER_DRAGON_GROWL");
        float volume = (float) config.getDouble("announce.sound.volume", 1.0);
        float pitch = (float) config.getDouble("announce.sound.pitch", 1.0);
        boolean showSeparator = config.getBoolean("announce.separator.enabled", true);
        String separatorTop = config.getString("announce.separator.top", "&e&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String separatorBottom = config.getString("announce.separator.bottom",
                "&e&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Formatter le message
        String formattedMessage = format
                .replace("{prefix}", messageManager.translateColors(prefix))
                .replace("{message}", messageManager.translateColors(message))
                .replace("{sender}", sender.getName());

        // Diffuser l'annonce
        if (showSeparator) {
            Bukkit.broadcastMessage(messageManager.translateColors(separatorTop));
        }

        Bukkit.broadcastMessage(formattedMessage);

        if (showSeparator) {
            Bukkit.broadcastMessage(messageManager.translateColors(separatorBottom));
        }

        // Jouer un son si activé
        if (enableSound) {
            try {
                Sound sound = Sound.valueOf(soundName);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), sound, volume, pitch);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Son invalide dans config.yml: " + soundName);
            }
        }

        // Message de confirmation au sender
        sender.sendMessage(messageManager.getMessage("announce.sent"));

        // Log dans la console
        plugin.logInfo("Annonce envoyée par " + sender.getName() + ": " + message);

        return true;
    }
}
