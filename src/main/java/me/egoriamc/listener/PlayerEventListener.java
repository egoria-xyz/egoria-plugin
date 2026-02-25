package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener pour les événements des joueurs
 */
public class PlayerEventListener implements Listener {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;

    public PlayerEventListener(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String message = messageManager.getJoinMessage(player);

        event.setJoinMessage(message);
        plugin.logInfo(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String message = messageManager.getLeaveMessage(player);

        event.setQuitMessage(message);
        plugin.logInfo(message);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        String killerName = "";
        if (killer != null) {
            killerName = killer.getName();
        }

        String cause = getDeathCause(event);
        String message = messageManager.getDeathMessage(player.getName(), cause, killerName);

        plugin.logInfo(message);
    }

    /**
     * Récupère la cause de la mort du joueur
     */
    private String getDeathCause(PlayerDeathEvent event) {
        org.bukkit.event.entity.EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();

        if (cause == null) {
            return "inconnu";
        }

        return cause.toString().toLowerCase();
    }
}
