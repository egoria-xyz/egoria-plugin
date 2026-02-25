package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.RecipeChoice;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Commande /furnace - Cuit automatiquement l'item en main
 * Seuls les joueurs avec le grade 'Gardien' peuvent utiliser cette commande
 */
public class FurnaceCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;

    // Mapping direct des minerais bruts vers les produits finis
    private static final Map<Material, Material> ORE_PRODUCTS = new HashMap<>();

    static {
        // Minerais bruts
        ORE_PRODUCTS.put(Material.RAW_IRON, Material.IRON_INGOT);
        ORE_PRODUCTS.put(Material.RAW_GOLD, Material.GOLD_INGOT);
        ORE_PRODUCTS.put(Material.RAW_COPPER, Material.COPPER_INGOT);
        ORE_PRODUCTS.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
    }

    public FurnaceCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c[EgoriaMC] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        // Vérifier la permission "Gardien"
        if (!player.hasPermission("egoriamc.furnace.use")) {
            player.sendMessage(messageManager
                    .translateColors("&c❌ Seuls les joueurs avec le grade 'Gardien' peuvent utiliser cette commande."));
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Vérifier si le joueur tient un item
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(messageManager
                    .translateColors("&c❌ Vous devez tenir un item dans votre main pour utiliser cette commande."));
            return true;
        }

        // Chercher une recette de four pour cet item
        ItemStack result = getCookedResult(itemInHand.getType());

        if (result == null) {
            player.sendMessage(messageManager.translateColors("&c❌ Cet item ne peut pas être cuit dans un four."));
            return true;
        }

        // Remplacer l'item en main par l'item cuit
        int amount = itemInHand.getAmount();
        result.setAmount(amount);
        player.getInventory().setItemInMainHand(result);

        player.sendMessage(messageManager.translateColors(
                "&a✓ Vous avez cuit &e" + amount + " &a" + getItemName(itemInHand.getType()) + "(s) avec succès!"));

        return true;
    }

    /**
     * Obtient le résultat cuit d'un item
     */
    private ItemStack getCookedResult(Material material) {
        // PREMIÈRE priorité: vérifier dans le mapping direct (minerais bruts)
        if (ORE_PRODUCTS.containsKey(material)) {
            Material product = ORE_PRODUCTS.get(material);
            plugin.getLogger().info("✓ Recette trouvée (mapping) pour " + material + " -> " + product);
            return new ItemStack(product);
        }

        // DEUXIÈME priorité: chercher dans les recettes de cuisson du serveur
        var recipeIterator = plugin.getServer().recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();

            // Vérifier si c'est une recette de cuisson (four, fourneau, fumoir, etc.)
            if (recipe instanceof CookingRecipe cookingRecipe) {
                try {
                    // Vérifier directement le matériau d'entrée
                    ItemStack input = cookingRecipe.getInput();
                    if (input != null && input.getType() == material) {
                        plugin.getLogger().info(
                                "✓ Recette trouvée (serveur) pour " + material + " -> "
                                        + cookingRecipe.getResult().getType());
                        return cookingRecipe.getResult().clone();
                    }

                    // Vérifier la RecipeChoice pour les recettes avec choix multiples
                    RecipeChoice choice = cookingRecipe.getInputChoice();
                    if (choice != null) {
                        ItemStack testItem = new ItemStack(material, 1);
                        if (choice.test(testItem)) {
                            plugin.getLogger().info("✓ Recette trouvée (RecipeChoice) pour " + material + " -> "
                                    + cookingRecipe.getResult().getType());
                            return cookingRecipe.getResult().clone();
                        }
                    }
                } catch (Exception e) {
                    // Ignorer les exceptions et continuer
                }
            }
        }
        return null;
    }

    /**
     * Obtient le nom formaté d'un item
     */
    private String getItemName(Material material) {
        String name = material.toString()
                .toLowerCase()
                .replace("_", " ");

        // Mettre en majuscule la première lettre de chaque mot
        Pattern pattern = Pattern.compile("\\b\\w");
        Matcher matcher = pattern.matcher(name);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group().toUpperCase());
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
