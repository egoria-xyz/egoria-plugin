package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Manager pour créer et gérer l'inventaire de réparation
 */
public class RepairInventoryManager implements InventoryHolder {

    private final EgoriaMC plugin;
    private static final String GUI_TITLE = "§6⚒ Réparation d'équipements";
    private static final int GUI_SIZE = 54;

    public RepairInventoryManager(EgoriaMC plugin) {
        this.plugin = plugin;
    }

    /**
     * Crée l'inventaire de réparation pour un joueur
     */
    public Inventory createRepairInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(this, GUI_SIZE, GUI_TITLE);

        // Remplir les bordures avec de la vitre grise
        ItemStack grayPane = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, grayPane);
            inventory.setItem(45 + i, grayPane);
        }
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, grayPane);
            inventory.setItem(i + 8, grayPane);
        }

        // Ajouter les instructions
        ItemStack instructions = createItem(Material.PAPER, "§e📖 Instructions",
                "Placez l'item à réparer dans le slot central",
                "Choisissez XP ou Minerais",
                " ",
                "§6Coûts de réparation:",
                "§7- XP: 10 XP par 10% de dégâts",
                "§7- Minerais: Dépend du type");
        inventory.setItem(2, instructions);

        // Ajouter les boutons de réparation
        ItemStack repairWithXP = createItem(Material.EXPERIENCE_BOTTLE, "§b⚡ Réparer avec XP");
        inventory.setItem(29, repairWithXP);

        ItemStack repairWithMineral = createItem(Material.DIAMOND_ORE, "§d⛏ Réparer avec Minerais");
        inventory.setItem(33, repairWithMineral);

        // Afficher les coûts
        ItemStack costsDisplay = createItem(Material.CLOCK, "§e💰 Coûts",
                " ",
                "§6Ajouter un item au slot central");
        inventory.setItem(6, costsDisplay);

        // Minerais disponibles (affichage)
        ItemStack mineralsInfo = createItem(Material.DIAMOND_ORE, "§d💎 Minerais acceptés",
                "§7Iron Ore, Gold Ore, Coal Ore,",
                "§7Lapis Ore, Diamond Ore,",
                "§7Redstone Ore, Copper Ore");
        inventory.setItem(51, mineralsInfo);

        return inventory;
    }

    /**
     * Crée un item avec un display name et une lore
     */
    private ItemStack createItem(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            if (lore.length > 0) {
                java.util.List<String> loreList = new java.util.ArrayList<>();
                for (String line : lore) {
                    loreList.add(line);
                }
                meta.setLore(loreList);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Obtient le slot central où placer l'item
     */
    public static int getCentralSlot() {
        return 22; // Slot central de l'inventaire 54 cases
    }

    /**
     * Obtient le slot du bouton "Réparer avec XP"
     */
    public static int getRepairWithXPSlot() {
        return 29;
    }

    /**
     * Obtient le slot du bouton "Réparer avec Minerais"
     */
    public static int getRepairWithMineralSlot() {
        return 33;
    }

    /**
     * Crée un inventaire d'affichage des coûts
     */
    public Inventory createCostDisplay(ItemStack item, String costType) {
        Inventory inventory = Bukkit.createInventory(this, 27, "§6Coûts de réparation");

        RepairManager repairManager = plugin.getRepairManager();

        if (repairManager.isRepairable(item)) {
            ItemStack repairItem = item.clone();
            inventory.setItem(13, repairItem);

            if ("xp".equals(costType)) {
                int xpCost = repairManager.getRepairXPCost(item);
                ItemStack costItem = createItem(Material.EXPERIENCE_BOTTLE, "§b💎 Coût en XP",
                        "§7Coût total: §b" + xpCost + " XP");
                inventory.setItem(15, costItem);
            } else if ("mineral".equals(costType)) {
                ItemStack costItem = createItem(Material.DIAMOND_ORE, "§d💎 Coûts en Minerais",
                        "§7Iron Ore: §6" + repairManager.getRepairMineralCost(item, Material.IRON_ORE),
                        "§7Gold Ore: §6" + repairManager.getRepairMineralCost(item, Material.GOLD_ORE),
                        "§7Diamond Ore: §6" + repairManager.getRepairMineralCost(item, Material.DIAMOND_ORE));
                inventory.setItem(15, costItem);
            }
        } else {
            ItemStack errorItem = createItem(Material.BARRIER, "§c❌ Erreur",
                    "Cet item ne peut pas être réparé");
            inventory.setItem(13, errorItem);
        }

        return inventory;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
