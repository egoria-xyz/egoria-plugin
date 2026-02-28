package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire de la GUI de la liste des warps
 */
public class WarpListGuiManager {

    private final EgoriaMC plugin;
    private final WarpManager warpManager;
    private final MessageManager messageManager;

    private static final int INVENTORY_SIZE = 27;
    private static final int ITEMS_PER_PAGE = 25;

    public WarpListGuiManager(EgoriaMC plugin) {
        this.plugin = plugin;
        this.warpManager = plugin.getWarpManager();
        this.messageManager = plugin.getMessageManager();
    }

    /**
     * Ouvre la GUI de la liste des warps
     */
    public void openWarpListGui(Player player, int page) {
        Inventory gui = createWarpListInventory(page);
        player.openInventory(gui);
    }

    /**
     * Crée l'inventaire de la liste des warps
     */
    public static Inventory createWarpListInventory(int page) {
        EgoriaMC plugin = EgoriaMC.getInstance();
        WarpManager warpManager = plugin.getWarpManager();

        // Récupérer tous les warps
        Map<String, Location> warps = warpManager.getAllWarps();
        List<WarpData> warpList = new ArrayList<>();

        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            String warpName = entry.getKey();
            Location location = entry.getValue();
            String creator = warpManager.getWarpCreator(warpName);
            warpList.add(new WarpData(warpName, location, creator));
        }

        // Si aucun warp
        if (warpList.isEmpty()) {
            Inventory emptyGui = Bukkit.createInventory(null, 9, "§e🌍 Warps");
            ItemStack emptyItem = new ItemStack(Material.BARRIER);
            ItemMeta meta = emptyItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§cAucun warp disponible");
                emptyItem.setItemMeta(meta);
            }
            emptyGui.setItem(4, emptyItem);
            return emptyGui;
        }

        // Trier alphabétiquement
        warpList.sort((a, b) -> a.name.compareToIgnoreCase(b.name));

        // Calculer le nombre de pages
        int totalPages = (int) Math.ceil((double) warpList.size() / ITEMS_PER_PAGE);
        if (page > totalPages)
            page = totalPages;
        if (page < 1)
            page = 1;

        // Créer le titre
        String title = "§e🌍 Warps (" + page + "/" + totalPages + ")";
        Inventory gui = Bukkit.createInventory(null, INVENTORY_SIZE, title);

        // Calculer indices
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, warpList.size());

        // Ajouter les items
        int slot = 0;
        for (int i = startIndex; i < endIndex && slot < 25; i++) {
            try {
                WarpData warp = warpList.get(i);
                ItemStack item = createWarpItem(warp);
                gui.setItem(slot, item);
                slot++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Boutons de navigation
        if (page > 1) {
            ItemStack previousButton = createNavigationButton("§e◄ Page Précédente", page - 1);
            gui.setItem(25, previousButton);
        }

        if (page < totalPages) {
            ItemStack nextButton = createNavigationButton("§eProchaine Page ►", page + 1);
            gui.setItem(26, nextButton);
        }

        return gui;
    }

    /**
     * Crée un item pour un warp
     */
    private static ItemStack createWarpItem(WarpData warp) {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§e🌍 " + warp.name);

        List<String> lore = new ArrayList<>();
        if (warp.location.getWorld() != null) {
            lore.add("§7Monde: §f" + warp.location.getWorld().getName());
        }
        lore.add("§7X: §f" + String.format("%.1f", warp.location.getX()));
        lore.add("§7Y: §f" + String.format("%.1f", warp.location.getY()));
        lore.add("§7Z: §f" + String.format("%.1f", warp.location.getZ()));
        lore.add("§7Créateur: §f" + warp.creator);
        lore.add("");
        lore.add("§6✠ Cliquez pour vous y téléporter");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Crée un bouton de navigation
     */
    private static ItemStack createNavigationButton(String displayName, int page) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();
        lore.add("§7Cliquez pour aller à la page " + page);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Structure de données pour un warp
     */
    public static class WarpData {
        public String name;
        public Location location;
        public String creator;

        public WarpData(String name, Location location, String creator) {
            this.name = name;
            this.location = location;
            this.creator = creator;
        }
    }
}
