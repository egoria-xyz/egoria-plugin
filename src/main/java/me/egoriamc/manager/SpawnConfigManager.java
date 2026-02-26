package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.EnumSet;
import java.util.Set;

/**
 * Gestionnaire de la configuration des taux de spawn (spawn.yml).
 */
public class SpawnConfigManager {

    private static final Set<EntityType> ANIMALS = EnumSet.of(
            EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.CHICKEN,
            EntityType.RABBIT, EntityType.FOX, EntityType.WOLF, EntityType.CAT,
            EntityType.DONKEY, EntityType.HORSE, EntityType.MULE, EntityType.LLAMA,
            EntityType.PANDA, EntityType.BEE, EntityType.GOAT, EntityType.FROG,
            EntityType.CAMEL, EntityType.SNIFFER, EntityType.OCELOT, EntityType.PARROT,
            EntityType.TURTLE, EntityType.POLAR_BEAR, EntityType.TRADER_LLAMA,
            EntityType.AXOLOTL, EntityType.MOOSHROOM
    );

    private static final Set<EntityType> MONSTERS = EnumSet.of(
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.ENDERMAN,
            EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.WITCH, EntityType.SLIME,
            EntityType.MAGMA_CUBE, EntityType.BLAZE, EntityType.GHAST, EntityType.PIGLIN,
            EntityType.HOGLIN, EntityType.ZOMBIFIED_PIGLIN, EntityType.PHANTOM,
            EntityType.DROWNED, EntityType.STRAY, EntityType.HUSK, EntityType.VEX,
            EntityType.EVOKER, EntityType.VINDICATOR, EntityType.PILLAGER,
            EntityType.RAVAGER, EntityType.WARDEN, EntityType.ENDERMITE,
            EntityType.SILVERFISH, EntityType.ZOGLIN, EntityType.PIGLIN_BRUTE
    );

    private static final Set<EntityType> WATER_ANIMALS = EnumSet.of(
            EntityType.SQUID, EntityType.DOLPHIN, EntityType.GLOW_SQUID
    );

    private static final Set<EntityType> AMBIENT = EnumSet.of(
            EntityType.BAT
    );

    private static final Set<EntityType> WATER_AMBIENT = EnumSet.of(
            EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.COD, EntityType.SALMON
    );

    private final EgoriaMC plugin;
    private FileConfiguration spawnConfig;
    private File spawnFile;

    public SpawnConfigManager(EgoriaMC plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
        if (!spawnFile.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource("spawn.yml", false);
        }
        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
    }

    public boolean isEnabled() {
        return spawnConfig.getBoolean("enabled", true);
    }

    /**
     * Retourne le multiplicateur pour un type d'entité.
     * Priorité : entities.* > categories.* > default
     */
    public double getMultiplier(EntityType entityType) {
        String entityKey = entityType.name();
        if (spawnConfig.contains("entities." + entityKey)) {
            return spawnConfig.getDouble("entities." + entityKey, 1.0);
        }
        String category = getCategory(entityType);
        if (category != null && spawnConfig.contains("categories." + category)) {
            return spawnConfig.getDouble("categories." + category, 1.0);
        }
        return spawnConfig.getDouble("default", 1.0);
    }

    private String getCategory(EntityType type) {
        if (ANIMALS.contains(type)) return "animals";
        if (MONSTERS.contains(type)) return "monsters";
        if (WATER_ANIMALS.contains(type)) return "water_animals";
        if (AMBIENT.contains(type)) return "ambient";
        if (WATER_AMBIENT.contains(type)) return "water_ambient";
        return null;
    }

    public FileConfiguration getConfig() {
        return spawnConfig;
    }
}
