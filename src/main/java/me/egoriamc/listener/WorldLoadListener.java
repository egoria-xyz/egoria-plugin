package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * Écoute le chargement des mondes
 */
public class WorldLoadListener implements Listener {

    private final EgoriaMC plugin;

    public WorldLoadListener(EgoriaMC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        // Recharger les homes et warps quand un monde se charge
        plugin.getHomeManager().reload();
        plugin.getWarpManager().reload();
    }
}
