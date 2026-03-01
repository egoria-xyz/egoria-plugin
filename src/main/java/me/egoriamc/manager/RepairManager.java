package me.egoriamc.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager pour la réparation des équipements
 * Gère les coûts en XP et minerais
 */
public class RepairManager {

    // Coûts de réparation en XP par 10% de durabilité
    private static final int XP_COST_PER_10_PERCENT = 10;

    // Minerais acceptés pour la réparation (valeur = nombre de minerais pour 10%)
    private static final Map<Material, Integer> REPAIR_MATERIALS = new HashMap<>();

    static {
        REPAIR_MATERIALS.put(Material.IRON_ORE, 2);
        REPAIR_MATERIALS.put(Material.GOLD_ORE, 3);
        REPAIR_MATERIALS.put(Material.COAL_ORE, 1);
        REPAIR_MATERIALS.put(Material.LAPIS_ORE, 2);
        REPAIR_MATERIALS.put(Material.DIAMOND_ORE, 5);
        REPAIR_MATERIALS.put(Material.REDSTONE_ORE, 3);
        REPAIR_MATERIALS.put(Material.COPPER_ORE, 1);
        REPAIR_MATERIALS.put(Material.DEEPSLATE_IRON_ORE, 2);
        REPAIR_MATERIALS.put(Material.DEEPSLATE_GOLD_ORE, 3);
        REPAIR_MATERIALS.put(Material.DEEPSLATE_COAL_ORE, 1);
        REPAIR_MATERIALS.put(Material.DEEPSLATE_LAPIS_ORE, 2);
        REPAIR_MATERIALS.put(Material.DEEPSLATE_DIAMOND_ORE, 5);
        REPAIR_MATERIALS.put(Material.DEEPSLATE_REDSTONE_ORE, 3);
        REPAIR_MATERIALS.put(Material.DEEPSLATE_COPPER_ORE, 1);
    }

    /**
     * Vérifie si un item peut être réparé
     */
    public boolean isRepairable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (!(itemMeta instanceof Damageable)) {
            return false;
        }

        Damageable damageable = (Damageable) itemMeta;
        return damageable.getDamage() > 0;
    }

    /**
     * Obtient le coût en XP pour réparer complètement un item
     */
    public int getRepairXPCost(ItemStack item) {
        if (!isRepairable(item)) {
            return 0;
        }

        ItemMeta itemMeta = item.getItemMeta();
        Damageable damageable = (Damageable) itemMeta;

        double maxDurability = item.getType().getMaxDurability();
        double damagePercent = (damageable.getDamage() / maxDurability) * 100;

        // Arrondir au 10% supérieur
        int damageIn10Percent = (int) Math.ceil(damagePercent / 10);

        return damageIn10Percent * XP_COST_PER_10_PERCENT;
    }

    /**
     * Coût en minerais pour réparer un item
     */
    public int getRepairMineralCost(ItemStack item, Material mineral) {
        if (!isRepairable(item) || !REPAIR_MATERIALS.containsKey(mineral)) {
            return 0;
        }

        int costPer10Percent = REPAIR_MATERIALS.get(mineral);
        ItemMeta itemMeta = item.getItemMeta();
        Damageable damageable = (Damageable) itemMeta;

        double maxDurability = item.getType().getMaxDurability();
        double damagePercent = (damageable.getDamage() / maxDurability) * 100;

        // Arrondir au 10% supérieur
        int damageIn10Percent = (int) Math.ceil(damagePercent / 10);

        return damageIn10Percent * costPer10Percent;
    }

    /**
     * Répare un item avec de l'XP
     */
    public boolean repairWithXP(Player player, ItemStack item) {
        if (!isRepairable(item)) {
            return false;
        }

        int xpRequired = getRepairXPCost(item);
        int playerXP = player.getTotalExperience();

        if (playerXP < xpRequired) {
            return false; // Pas assez d'XP
        }

        // Réparer l'item
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta instanceof Damageable) {
            Damageable damageable = (Damageable) itemMeta;
            damageable.setDamage(0);
            item.setItemMeta(itemMeta);
        }

        // Retirer l'XP
        player.setTotalExperience(playerXP - xpRequired);

        return true;
    }

    /**
     * Répare un item avec des minerais
     */
    public boolean repairWithMineral(Player player, ItemStack item, Material mineral) {
        if (!isRepairable(item)) {
            return false;
        }

        if (!REPAIR_MATERIALS.containsKey(mineral)) {
            return false; // Minerai non accepté
        }

        int requiredAmount = getRepairMineralCost(item, mineral);

        // Compter le nombre total de minerais du joueur
        int playerAmount = 0;
        for (ItemStack invItem : player.getInventory().all(mineral).values()) {
            playerAmount += invItem.getAmount();
        }

        if (playerAmount < requiredAmount) {
            return false; // Pas assez de minerais
        }

        // Réparer l'item
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta instanceof Damageable) {
            Damageable damageable = (Damageable) itemMeta;
            damageable.setDamage(0);
            item.setItemMeta(itemMeta);
        }

        // Retirer les minerais
        player.getInventory().removeItem(new ItemStack(mineral, requiredAmount));

        return true;
    }

    /**
     * Obtient les minerais acceptés
     */
    public static Map<Material, Integer> getRepairMaterials() {
        return new HashMap<>(REPAIR_MATERIALS);
    }

    /**
     * Vérifie si un minerai est accepté
     */
    public static boolean isAcceptedMineral(Material material) {
        return REPAIR_MATERIALS.containsKey(material);
    }

    /**
     * Calcule le pourcentage de dégâts d'un item
     */
    public double getDamagePercent(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return 0;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (!(itemMeta instanceof Damageable)) {
            return 0;
        }

        Damageable damageable = (Damageable) itemMeta;
        double maxDurability = item.getType().getMaxDurability();

        return (damageable.getDamage() / maxDurability) * 100;
    }
}
