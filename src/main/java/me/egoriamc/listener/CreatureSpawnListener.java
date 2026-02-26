package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.SpawnConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

/**
 * Applique les multiplicateurs de spawn définis dans spawn.yml
 * (réduire, augmenter ou booster le taux de spawn des animaux et mobs).
 */
public class CreatureSpawnListener implements Listener {

    private final EgoriaMC plugin;
    private final SpawnConfigManager spawnConfig;
    private final Random random = new Random();

    public CreatureSpawnListener(EgoriaMC plugin) {
        this.plugin = plugin;
        this.spawnConfig = plugin.getSpawnConfigManager();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!spawnConfig.isEnabled()) {
            return;
        }
        // Ne modifier que les spawns naturels (pas les œufs, spawners, reproduction, etc.)
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL
                && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DEFAULT) {
            return;
        }

        EntityType type = event.getEntityType();
        double multiplier = spawnConfig.getMultiplier(type);

        if (multiplier <= 0) {
            event.setCancelled(true);
            return;
        }

        if (multiplier < 1.0) {
            // Réduire : on laisse passer avec une probabilité = multiplicateur
            if (random.nextDouble() >= multiplier) {
                event.setCancelled(true);
            }
            return;
        }

        if (multiplier > 1.0) {
            // Booster : spawn autorisé + probabilité de spawn supplémentaire
            double extraChance = multiplier - 1.0;
            if (extraChance >= 1.0 || random.nextDouble() < extraChance) {
                scheduleExtraSpawn(event.getLocation(), type);
            }
        }
    }

    /**
     * Programme un spawn supplémentaire au tick suivant pour éviter les conflits.
     */
    private void scheduleExtraSpawn(Location baseLocation, EntityType type) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Location spawnLoc = findSpawnLocation(baseLocation);
            if (spawnLoc != null && spawnLoc.getWorld() != null) {
                spawnLoc.getWorld().spawnEntity(spawnLoc, type);
            }
        });
    }

    /**
     * Retourne un emplacement décalé à côté du spawn initial (évite le stacking).
     */
    private Location findSpawnLocation(Location base) {
        int dx = random.nextInt(3) - 1;
        int dz = random.nextInt(3) - 1;
        if (dx == 0 && dz == 0) {
            dz = 1;
        }
        return base.clone().add(dx, 0, dz);
    }
}
