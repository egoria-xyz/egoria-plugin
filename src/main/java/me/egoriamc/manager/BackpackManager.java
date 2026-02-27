package me.egoriamc.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Gestionnaire des backpacks des joueurs
 */
public class BackpackManager {

    private final JavaPlugin plugin;
    private final File backpacksFolder;
    private final Map<UUID, Set<Integer>> unlockedSlots = new HashMap<>();
    private static final double BASE_PRICE = 100;
    private static final double EXPONENTIAL_FACTOR = 1.5;

    public BackpackManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.backpacksFolder = new File(plugin.getDataFolder(), "backpacks");

        if (!backpacksFolder.exists()) {
            backpacksFolder.mkdirs();
        }

        // Charger les données de tous les joueurs
        loadAllBackpacks();
    }

    /**
     * Charge tous les backpacks des fichiers
     */
    private void loadAllBackpacks() {
        File[] files = backpacksFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    try {
                        UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                        loadBackpackData(uuid);
                    } catch (IllegalArgumentException ignored) {
                        // Fichier invalide, ignorer
                    }
                }
            }
        }
    }

    /**
     * Charge les données du backpack d'un joueur
     */
    public void loadBackpackData(UUID uuid) {
        File file = new File(backpacksFolder, uuid + ".yml");
        if (!file.exists()) {
            // Créer un nouveau backpack avec un slot déverrouillé
            Set<Integer> slots = new HashSet<>();
            slots.add(0); // Premier slot toujours déverrouillé
            unlockedSlots.put(uuid, slots);
            saveBackpackData(uuid);
            plugin.getLogger().info("Backpack créé avec slot 0 déverrouillé pour " + uuid);
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            Set<Integer> slots = new HashSet<>(config.getIntegerList("unlocked-slots"));

            // Migration: nettoyer les données corrompues
            // Assurer que les slots sont déverrouillés séquentiellement à partir de 0
            if (slots.isEmpty() || !slots.contains(0)) {
                plugin.getLogger().warning("Migration: Réinitialisation du backpack pour " + uuid);
                slots = new HashSet<>();
                slots.add(0);
            }

            // Valider et nettoyer les slots
            Set<Integer> validSlots = new HashSet<>();
            for (int slot : slots) {
                if (slot >= 0 && slot < 54) { // Max 54 slots (6 lignes)
                    validSlots.add(slot);
                }
            }

            // S'assurer que le slot 0 est présent
            if (!validSlots.contains(0)) {
                validSlots.add(0);
            }

            unlockedSlots.put(uuid, validSlots);
            plugin.getLogger().info("Backpack chargé pour " + uuid + " slots: " + validSlots);
        }
    }

    /**
     * Sauvegarde les données du backpack d'un joueur
     */
    public void saveBackpackData(UUID uuid) {
        File file = new File(backpacksFolder, uuid + ".yml");
        FileConfiguration config = new YamlConfiguration();

        Set<Integer> slots = unlockedSlots.getOrDefault(uuid, new HashSet<>());
        config.set("unlocked-slots", new java.util.ArrayList<>(slots));

        try {
            config.save(file);
            plugin.getLogger().info("Backpack sauvegardé pour " + uuid + " avec slots: " + slots);
        } catch (IOException e) {
            plugin.getLogger().warning("Impossible de sauvegarder le backpack de " + uuid);
            e.printStackTrace();
        }
    }

    /**
     * Obtient le nombre de lignes du backpack pour un joueur
     */
    public int getBackpackLines(Player player) {
        // Vérifier si streamer
        if (player.hasPermission("group.streamer") || player.hasPermission("streamer")) {
            return 2; // 2 lignes = 18 slots
        }
        return 1; // 1 ligne = 9 slots
    }

    /**
     * Obtient le nombre de slots disponibles pour un joueur
     */
    public int getTotalSlots(Player player) {
        return getBackpackLines(player) * 9;
    }

    /**
     * Vérifie si un slot est déverrouillé
     */
    public boolean isSlotUnlocked(UUID uuid, int slot) {
        return unlockedSlots.getOrDefault(uuid, new HashSet<>()).contains(slot);
    }

    /**
     * Obtient les slots déverrouillés
     */
    public Set<Integer> getUnlockedSlots(UUID uuid) {
        return new HashSet<>(unlockedSlots.getOrDefault(uuid, new HashSet<>()));
    }

    /**
     * Calcule le prix pour déverrouiller un slot
     */
    public double getPriceForSlot(int slot) {
        // Prix = base * (factor ^ slot)
        return BASE_PRICE * Math.pow(EXPONENTIAL_FACTOR, slot);
    }

    /**
     * Déverrouille un slot pour un joueur
     */
    public boolean unlockSlot(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        Set<Integer> slots = unlockedSlots.getOrDefault(uuid, new HashSet<>());

        // Vérifier que le slot est valide
        int totalSlots = getTotalSlots(player);
        if (slot < 0 || slot >= totalSlots) {
            plugin.getLogger()
                    .warning("Slot invalide pour " + player.getName() + ": " + slot + " (total: " + totalSlots + ")");
            return false;
        }

        // Vérifier que le slot n'est pas déjà déverrouillé
        if (slots.contains(slot)) {
            plugin.getLogger().warning("Slot déjà déverrouillé pour " + player.getName() + ": " + slot);
            return false;
        }

        // Vérifier que tous les slots antérieurs sont déverrouillés
        for (int i = 0; i < slot; i++) {
            if (!slots.contains(i)) {
                plugin.getLogger().warning(
                        "Slot " + i + " pas déverrouillé pour " + player.getName() + " (tentative: " + slot + ")");
                plugin.getLogger().warning("Slots déverrouillés: " + slots);
                return false;
            }
        }

        slots.add(slot);
        unlockedSlots.put(uuid, slots);
        saveBackpackData(uuid);
        plugin.getLogger().info("Slot " + slot + " déverrouillé pour " + player.getName());
        return true;
    }

    /**
     * Obtient le prochain slot à déverrouiller
     */
    public int getNextLockedSlot(UUID uuid, Player player) {
        Set<Integer> unlockedSet = unlockedSlots.getOrDefault(uuid, new HashSet<>());
        int totalSlots = getTotalSlots(player);

        for (int i = 0; i < totalSlots; i++) {
            if (!unlockedSet.contains(i)) {
                return i;
            }
        }

        return -1; // Tous les slots sont déverrouillés
    }
}
