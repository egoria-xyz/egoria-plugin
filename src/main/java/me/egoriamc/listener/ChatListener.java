package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.util.EmojiUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Listener pour remplacer les codes emojis Discord-like (:emoji:) dans le chat
 */
public class ChatListener implements Listener {

    private final EgoriaMC plugin;

    public ChatListener(EgoriaMC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String message = event.getMessage();

        // Remplacer les codes emojis :emoji: par les emojis Unicode
        String processedMessage = EmojiUtil.replaceEmojis(message);

        // Mettre à jour le message si des emojis ont été remplacés
        if (!message.equals(processedMessage)) {
            event.setMessage(processedMessage);
        }
    }
}
