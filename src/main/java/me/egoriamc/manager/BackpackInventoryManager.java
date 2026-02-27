package me.egoriamc.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestionnaire de sérialisation des items du backpack
 */
public class BackpackInventoryManager {

    private final JavaPlugin plugin;
    private final File inventoriesFolder;

    public BackpackInventoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.inventoriesFolder = new File(plugin.getDataFolder(), "backpack-inventories");

        if (!inventoriesFolder.exists()) {
            inventoriesFolder.mkdirs();
        }
    }

    /**
     * Sauvegarde l'inventaire du backpack d'un joueur
     */
    public void saveBackpackInventory(UUID uuid, Map<Integer, ItemStack> items) {
        File file = new File(inventoriesFolder, uuid + ".yml");
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null && item.getType().isAir()) {
                continue;
            }

            String encoded = encodeItemStack(item);
            if (encoded != null) {
                config.set("items." + entry.getKey(), encoded);
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Impossible de sauvegarder l'inventaire du backpack de " + uuid);
            e.printStackTrace();
        }
    }

    /**
     * Charge l'inventaire du backpack d'un joueur
     */
    public Map<Integer, ItemStack> loadBackpackInventory(UUID uuid) {
        Map<Integer, ItemStack> items = new HashMap<>();
        File file = new File(inventoriesFolder, uuid + ".yml");

        if (!file.exists()) {
            return items;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("items")) {
            for (String key : config.getConfigurationSection("items").getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    String encoded = config.getString("items." + key);
                    ItemStack item = decodeItemStack(encoded);

                    // Ignorer les items corrompus (BARRIER, VITRE GRISE)
                    if (item != null && !item.getType().name().contains("BARRIER")
                            && !item.getType().name().contains("GLASS_PANE")) {
                        items.put(slot, item);
                    } else if (item != null) {
                        plugin.getLogger().warning("Nettoyage: Item UI trouvé au slot " + slot + " pour " + uuid);
                    }
                } catch (NumberFormatException ignored) {
                    // Clé invalide
                }
            }
        }

        return items;
    }

    /**
     * Encode un ItemStack en Base64
     */
    private String encodeItemStack(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            plugin.getLogger().warning("Impossible d'encoder un ItemStack");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decode un ItemStack depuis Base64
     */
    private ItemStack decodeItemStack(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            plugin.getLogger().warning("Impossible de décoder un ItemStack");
            return null;
        }
    }
}
