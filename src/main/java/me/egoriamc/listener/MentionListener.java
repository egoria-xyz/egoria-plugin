package me.egoriamc.listener;

import me.egoriamc.EgoraIMC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Listener pour le système de mentions (@pseudo)
 */
public class MentionListener implements Listener {

    private final EgoraIMC plugin;
    private final Pattern mentionPattern = Pattern.compile("@([a-zA-Z0-9_]{1,16})");

    public MentionListener(EgoraIMC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        // Vérifier si les mentions sont activées
        if (!plugin.getConfigManager().getConfig().getBoolean("mentions.enabled", true)) {
            return;
        }

        // Trouver toutes les mentions dans le message
        Matcher matcher = mentionPattern.matcher(message);
        String modifiedMessage = message;

        while (matcher.find()) {
            String mention = matcher.group(1); // Le pseudo sans le @
            Player mentionedPlayer = Bukkit.getPlayer(mention);

            if (mentionedPlayer != null && mentionedPlayer.isOnline()) {
                // Remplacer la mention par une version colorée en rouge foncé
                String coloredMention = "§4@" + mention + "§r";
                modifiedMessage = modifiedMessage.replace("@" + mention, coloredMention);

                // Envoyer une notification au joueur mentionné (async safe)
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // Jouer un son configurable
                    String sound = plugin.getConfigManager().getConfig()
                        .getString("mentions.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    float volume = (float) plugin.getConfigManager().getConfig()
                        .getDouble("mentions.volume", 1.0);
                    float pitch = (float) plugin.getConfigManager().getConfig()
                        .getDouble("mentions.pitch", 1.0);

                    try {
                        Sound notificationSound = Sound.valueOf(sound);
                        mentionedPlayer.playSound(mentionedPlayer.getLocation(), notificationSound, volume, pitch);
                    } catch (IllegalArgumentException e) {
                        plugin.logError("Son invalide dans la config: " + sound);
                    }

                    // Envoyer un message au joueur mentionné
                    mentionedPlayer.sendMessage("§6➤ §e" + sender.getName() + "§f vous a mentionné!");
                });
            }
        }

        // Mettre à jour le message avec les mentions colorées
        event.setMessage(modifiedMessage);
    }
}
