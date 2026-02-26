package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.command.BackpackCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener pour sauvegarder le backpack lors de la mort et lors de la
 * déconnexion
 */
public class BackpackSaveListener implements Listener {

    private final EgoriaMC plugin;
    private static final Map<UUID, Long> lastSaveTime = new HashMap<>();

    public BackpackSaveListener(EgoriaMC plugin) {
        this.plugin = plugin;
        startAutoSaveTask();
    }

    /**
     * Démarre la tâche d'auto-sauvegarde
     */
    private void startAutoSaveTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Sauvegarder tous les joueurs qui ont un backpack ouvert
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getOpenInventory().getTitle().contains("Backpack")) {
                    BackpackCommand command = new BackpackCommand(plugin);
                    command.saveBackpackInventory(player);
                }
            }
        }, 0L, 600L); // Exécuter toutes les 30 secondes (600 ticks = 30s)
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Sauvegarder les items du backpack avant la mort
        // Cela évite que les items non sauvegardés soient perdus
        BackpackCommand command = new BackpackCommand(plugin);
        command.saveBackpackInventory(player);

        player.sendMessage("§6✓ &aVotre backpack a été sauvegardé.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Sauvegarder le backpack quand le joueur quitte le serveur
        BackpackCommand command = new BackpackCommand(plugin);
        command.saveBackpackInventory(player);

        // Nettoyer les données du joueur
        lastSaveTime.remove(player.getUniqueId());
    }
}
