package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gestionnaire des warps (points de téléportation publics)
 */
public class WarpManager {

    private final EgoriaMC plugin;
    private final File warpsFile;
    private FileConfiguration warpsConfig;
    private final Map<String, Location> warpsCache;

    public WarpManager(EgoriaMC plugin) {
        this.plugin = plugin;
        this.warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        this.warpsCache = new HashMap<>();

        if (!warpsFile.exists()) {
            try {
                warpsFile.createNewFile();
            } catch (IOException e) {
                plugin.logError("Erreur lors de la création du fichier warps.yml");
                e.printStackTrace();
            }
        }

        // Retarder le chargement d'un tick pour que tous les mondes soient chargés
        Bukkit.getScheduler().runTaskLater(plugin, this::reload, 1L);
    }

    public void reload() {
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        warpsCache.clear();

        // Charger tous les warps en cache
        if (warpsConfig.contains("warps")) {
            for (String warpName : warpsConfig.getConfigurationSection("warps").getKeys(false)) {
                try {
                    Location loc = warpsConfig.getLocation("warps." + warpName + ".location");
                    if (loc != null) {
                        warpsCache.put(warpName, loc);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.logError("⚠️  → Impossible de charger le warp '" + warpName + "': monde inexistant");
                }
            }
        }
    }

    /**
     * Récupère tous les warps disponibles
     */
    public Map<String, Location> getAllWarps() {
        return new HashMap<>(warpsCache);
    }

    /**
     * Récupère un warp spécifique
     */
    public Location getWarp(String warpName) {
        return warpsCache.get(warpName);
    }

    /**
     * Crée un nouveau warp
     */
    public boolean createWarp(String warpName, Location location, String creatorName) {
        // Vérifier si le warp existe déjà
        if (warpsCache.containsKey(warpName)) {
            return false;
        }

        warpsCache.put(warpName, location);
        saveWarp(warpName, location, creatorName);
        return true;
    }

    /**
     * Met à jour un warp existant
     */
    public boolean updateWarp(String warpName, Location location) {
        if (!warpsCache.containsKey(warpName)) {
            return false;
        }

        warpsCache.put(warpName, location);
        String creatorName = warpsConfig.getString("warps." + warpName + ".creator", "Unknown");
        saveWarp(warpName, location, creatorName);
        return true;
    }

    /**
     * Supprime un warp
     */
    public boolean deleteWarp(String warpName) {
        if (!warpsCache.containsKey(warpName)) {
            return false;
        }

        warpsCache.remove(warpName);
        warpsConfig.set("warps." + warpName, null);

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.logError("Erreur lors de la sauvegarde des warps");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Enregistre un warp dans le fichier de configuration
     */
    private void saveWarp(String warpName, Location location, String creatorName) {
        String warpPath = "warps." + warpName;

        // Sauvegarder le warp
        warpsConfig.set(warpPath + ".location", location);
        // Sauvegarder creator et date pour la traçabilité
        warpsConfig.set(warpPath + ".creator", creatorName);
        warpsConfig.set(warpPath + ".created-date",
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.logError("Erreur lors de la sauvegarde du warp");
            e.printStackTrace();
        }
    }

    /**
     * Récupère le créateur d'un warp
     */
    public String getWarpCreator(String warpName) {
        return warpsConfig.getString("warps." + warpName + ".creator", "Unknown");
    }

    /**
     * Sauvegarde tous les warps (appelé à la désactivation du plugin)
     */
    public void saveLater() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                warpsConfig.save(warpsFile);
                plugin.logInfo("&eWarps sauvegardés.");
            } catch (IOException e) {
                plugin.logError("Erreur lors de la sauvegarde finale des warps");
                e.printStackTrace();
            }
        });
    }
}
