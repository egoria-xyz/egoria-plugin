package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gestionnaire des homes des joueurs
 */
public class HomeManager {

    private final EgoriaMC plugin;
    private final File homesFile;
    private FileConfiguration homesConfig;
    private final Map<UUID, Map<String, Location>> homesCache;

    public HomeManager(EgoriaMC plugin) {
        this.plugin = plugin;
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
        this.homesCache = new HashMap<>();

        if (!homesFile.exists()) {
            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                plugin.logError("🚩 → Erreur lors de la création du fichier homes.yml");
                e.printStackTrace();
            }
        }

        // Retarder le chargement d'un tick pour que tous les mondes soient chargés
        Bukkit.getScheduler().runTaskLater(plugin, this::reload, 1L);
    }

    public void reload() {
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        homesCache.clear();

        // Charger tous les homes en cache
        for (String uuidStr : homesConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Location> playerHomes = new HashMap<>();

                if (homesConfig.contains(uuidStr)) {
                    for (String homeName : homesConfig.getConfigurationSection(uuidStr).getKeys(false)) {
                        // Ignorer la clé "username" - elle est juste pour la lisibilité
                        if ("username".equals(homeName)) {
                            continue;
                        }
                        try {
                            Location loc = homesConfig.getLocation(uuidStr + "." + homeName + ".location");
                            if (loc != null) {
                                playerHomes.put(homeName, loc);
                            }
                        } catch (IllegalArgumentException e) {
                            plugin.logError("⚠️  → Impossible de charger le home '" + homeName + "' du joueur "
                                    + uuidStr + ": monde inexistant");
                        }
                    }
                }

                if (!playerHomes.isEmpty()) {
                    homesCache.put(uuid, playerHomes);
                }
            } catch (IllegalArgumentException e) {
                plugin.logError("🚩 → UUID invalide dans homes.yml: " + uuidStr);
            }
        }
    }

    /**
     * Récupère tous les homes d'un joueur
     */
    public Map<String, Location> getPlayerHomes(UUID playerUuid) {
        return homesCache.getOrDefault(playerUuid, new HashMap<>());
    }

    /**
     * Récupère un home spécifique
     */
    public Location getHome(UUID playerUuid, String homeName) {
        Map<String, Location> playerHomes = getPlayerHomes(playerUuid);
        return playerHomes.get(homeName);
    }

    /**
     * Crée un nouveau home
     */
    public boolean createHome(Player player, String homeName, Location location) {
        UUID uuid = player.getUniqueId();
        Map<String, Location> playerHomes = homesCache.computeIfAbsent(uuid, k -> new HashMap<>());

        // Vérifier la limite
        int maxHomes = plugin.getConfigManager().getMaxHomes();
        if (playerHomes.size() >= maxHomes && !playerHomes.containsKey(homeName)) {
            return false;
        }

        playerHomes.put(homeName, location);
        saveHome(uuid, player.getName(), homeName, location);
        return true;
    }

    /**
     * Supprime un home
     */
    public boolean deleteHome(UUID playerUuid, String homeName) {
        Map<String, Location> playerHomes = homesCache.getOrDefault(playerUuid, new HashMap<>());

        if (!playerHomes.containsKey(homeName)) {
            return false;
        }

        playerHomes.remove(homeName);
        homesConfig.set(playerUuid.toString() + "." + homeName, null);

        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.logError("🚩 → Erreur lors de la sauvegarde des homes");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Enregistre un home dans le fichier de configuration
     */
    private void saveHome(UUID uuid, String username, String homeName, Location location) {
        String uuidStr = uuid.toString();
        String homePath = uuidStr + "." + homeName + ".location";
        String usernamePath = uuidStr + ".username";

        // Sauvegarder le home
        homesConfig.set(homePath, location);
        // Sauvegarder le username pour la lisibilité du fichier
        homesConfig.set(usernamePath, username);

        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.logError("🚩 → Erreur lors de la sauvegarde du home");
            e.printStackTrace();
        }
    }

    /**
     * Sauvegarde tous les homes (appelé à la désactivation du plugin)
     */
    public void saveLater() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                homesConfig.save(homesFile);
                plugin.logInfo("🚩 → &eHomes sauvegardés.");
            } catch (IOException e) {
                plugin.logError("🚩 → Erreur lors de la sauvegarde finale des homes");
                e.printStackTrace();
            }
        });
    }
}
