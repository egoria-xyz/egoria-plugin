package me.egoriamc;

import me.egoriamc.command.AddMoneyCommand;
import me.egoriamc.command.AddMoneyCommandTabCompleter;
import me.egoriamc.command.AnnounceCommand;
import me.egoriamc.command.BackpackCommand;
import me.egoriamc.command.BalanceCommand;
import me.egoriamc.command.BalanceCommandTabCompleter;
import me.egoriamc.command.BalanceTopCommand;
import me.egoriamc.command.CraftCommand;
import me.egoriamc.command.FurnaceCommand;
import me.egoriamc.command.HomeCommand;
import me.egoriamc.command.HomeCommandTabCompleter;
import me.egoriamc.command.HelpCommand;
import me.egoriamc.command.LiveCommand;
import me.egoriamc.command.PluginsCommand;
import me.egoriamc.command.RemoveMoneyCommand;
import me.egoriamc.command.RemoveMoneyCommandTabCompleter;
import me.egoriamc.command.ReloadCommand;
import me.egoriamc.command.Vote2SleepCommand;
import me.egoriamc.command.WarnCommand;
import me.egoriamc.command.WarnCommandTabCompleter;
import me.egoriamc.command.WarpCommand;
import me.egoriamc.command.WarpCommandTabCompleter;
import me.egoriamc.listener.BackpackInventoryListener;
import me.egoriamc.listener.BackpackSaveListener;
import me.egoriamc.listener.BalanceTopGuiListener;
import me.egoriamc.listener.ChatListener;
import me.egoriamc.listener.CreatureSpawnListener;
import me.egoriamc.listener.HomeInventoryListener;
import me.egoriamc.listener.HelpGuiListener;
import me.egoriamc.listener.PlayerEventListener;
import me.egoriamc.listener.MentionListener;
import me.egoriamc.listener.PluginsInventoryListener;
import me.egoriamc.listener.WorldLoadListener;
import me.egoriamc.listener.WarpListGuiListener;
import me.egoriamc.manager.AutoMessageManager;
import me.egoriamc.manager.BackpackInventoryManager;
import me.egoriamc.manager.BackpackManager;
import me.egoriamc.manager.BalanceTopGuiManager;
import me.egoriamc.manager.ConfigManager;
import me.egoriamc.manager.DatabaseManager;
import me.egoriamc.manager.EconomyManager;
import me.egoriamc.manager.HelpGuiManager;
import me.egoriamc.manager.SpawnConfigManager;
import me.egoriamc.manager.WarnManager;
import me.egoriamc.manager.HomeManager;
import me.egoriamc.manager.MessageManager;
import me.egoriamc.manager.WarpManager;
import me.egoriamc.manager.WarpListGuiManager;
import me.egoriamc.util.EmojiUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class EgoriaMC extends JavaPlugin {

    private static EgoriaMC instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private WarpListGuiManager warpListGuiManager;
    private AutoMessageManager autoMessageManager;
    private SpawnConfigManager spawnConfigManager;
    private DatabaseManager databaseManager;
    private WarnManager warnManager;
    private BackpackManager backpackManager;
    private BackpackInventoryManager backpackInventoryManager;
    private EconomyManager economyManager;
    private HelpGuiManager helpGuiManager;
    private BalanceTopGuiManager balanceTopGuiManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            // Initialiser les gestionnaires
            this.configManager = new ConfigManager(this);
            this.messageManager = new MessageManager(this);
            this.homeManager = new HomeManager(this);
            this.warpManager = new WarpManager(this);
            this.warpListGuiManager = new WarpListGuiManager(this);
            this.autoMessageManager = new AutoMessageManager(this);
            this.spawnConfigManager = new SpawnConfigManager(this);
            this.databaseManager = new DatabaseManager(this);
            this.warnManager = new WarnManager(this);
            this.backpackManager = new BackpackManager(this);
            this.backpackInventoryManager = new BackpackInventoryManager(this);
            this.economyManager = new EconomyManager(this);
            this.helpGuiManager = new HelpGuiManager(this);
            this.balanceTopGuiManager = new BalanceTopGuiManager(this);

            // Charger les emojis depuis emojis.yml
            EmojiUtil.loadEmojis(this);

            // Enregistrer les recettes de cuisson manquantes
            registerCookingRecipes();

            // Enregistrer les commandes
            getCommand("home").setExecutor(new HomeCommand(this));
            getCommand("home").setTabCompleter(new HomeCommandTabCompleter(this));
            getCommand("warp").setExecutor(new WarpCommand(this));
            getCommand("warp").setTabCompleter(new WarpCommandTabCompleter(this));
            getCommand("help").setExecutor(new HelpCommand(this));
            getCommand("plugins").setExecutor(new PluginsCommand());
            getCommand("annonce").setExecutor(new AnnounceCommand(this));
            getCommand("furnace").setExecutor(new FurnaceCommand(this));
            getCommand("craft").setExecutor(new CraftCommand(this));
            getCommand("live").setExecutor(new LiveCommand(this));
            getCommand("reload").setExecutor(new ReloadCommand(this));
            getCommand("warn").setExecutor(new WarnCommand(this));
            getCommand("warn").setTabCompleter(new WarnCommandTabCompleter());
            getCommand("vote2sleep").setExecutor(new Vote2SleepCommand(this));
            getCommand("backpack").setExecutor(new BackpackCommand(this));
            getCommand("balance").setExecutor(new BalanceCommand(this));
            getCommand("balance").setTabCompleter(new BalanceCommandTabCompleter());
            getCommand("money").setExecutor(new BalanceCommand(this));
            getCommand("money").setTabCompleter(new BalanceCommandTabCompleter());
            getCommand("bal").setExecutor(new BalanceCommand(this));
            getCommand("bal").setTabCompleter(new BalanceCommandTabCompleter());
            getCommand("baltop").setExecutor(new BalanceTopCommand(this));
            getCommand("moneytop").setExecutor(new BalanceTopCommand(this));
            getCommand("addmoney").setExecutor(new AddMoneyCommand(this));
            getCommand("addmoney").setTabCompleter(new AddMoneyCommandTabCompleter());
            getCommand("removemoney").setExecutor(new RemoveMoneyCommand(this));
            getCommand("removemoney").setTabCompleter(new RemoveMoneyCommandTabCompleter());

            // Enregistrer les événements
            getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
            getServer().getPluginManager().registerEvents(new MentionListener(this), this);
            getServer().getPluginManager().registerEvents(new ChatListener(), this);
            getServer().getPluginManager().registerEvents(new HomeInventoryListener(this), this);
            getServer().getPluginManager().registerEvents(new PluginsInventoryListener(), this);
            getServer().getPluginManager().registerEvents(new BackpackInventoryListener(this), this);
            getServer().getPluginManager().registerEvents(new BackpackSaveListener(this), this);
            getServer().getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
            getServer().getPluginManager().registerEvents(new HelpGuiListener(helpGuiManager), this);
            getServer().getPluginManager().registerEvents(new BalanceTopGuiListener(), this);
            getServer().getPluginManager().registerEvents(new WorldLoadListener(this), this);
            getServer().getPluginManager().registerEvents(new WarpListGuiListener(), this);

            logInfo("&ePlugin activé avec succès !");
            logInfo("&e- Gestion des homes : &aACTIVÉE");
            logInfo("&e- Gestion des warps : &aACTIVÉE");
            logInfo("&e- Gestion des messages : &aACTIVÉE");
            logInfo("&e- Messages automatiques : &aACTIVÉS");
            logInfo("&e- Taux de spawn (spawn.yml) : &aACTIVÉ");
            logInfo("&e- Warns (BDD) : "
                    + (databaseManager.isConfigured() ? "&aACTIVÉ" : "&7désactivé (database.yml)"));
            logInfo("&eUtilisez &b/help &epour voir l'aide !");

        } catch (Exception e) {
            logError("Erreur lors de l'initialisation du plugin");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (homeManager != null) {
            homeManager.saveLater();
        }
        if (warpManager != null) {
            warpManager.saveLater();
        }
        if (autoMessageManager != null) {
            autoMessageManager.stop();
        }
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
        logInfo("Plugin désactivé.");
    }

    /**
     * Affiche un message d'information avec emoji
     */
    public void logInfo(String message) {
        Bukkit.getConsoleSender().sendMessage("§ f[EgoriaMC] " + translateHexColorCodes(message));
    }

    /**
     * Affiche un message d'erreur
     */
    public void logError(String message) {
        Bukkit.getConsoleSender().sendMessage("§c[EgoriaMC] " + translateHexColorCodes(message));
    }

    /**
     * Traduit les codes de couleur Bukkit
     */
    public String translateHexColorCodes(String message) {
        return message.replace("&", "§");
    }

    /**
     * Enregistre les recettes de cuisson manquantes pour les minerais et ancient
     * debris
     */
    private void registerCookingRecipes() {
        try {
            int count = 0;

            // Recette pour RAW_IRON -> IRON_INGOT (Blast Furnace)
            if (Material.RAW_IRON != null && Material.IRON_INGOT != null) {
                BlastingRecipe rawIronRecipe = new BlastingRecipe(
                        new NamespacedKey(this, "raw_iron_blasting"),
                        new ItemStack(Material.IRON_INGOT),
                        Material.RAW_IRON,
                        0.7f,
                        100);
                getServer().addRecipe(rawIronRecipe);
                count++;
            }

            // Recette pour RAW_GOLD -> GOLD_INGOT (Blast Furnace)
            if (Material.RAW_GOLD != null && Material.GOLD_INGOT != null) {
                BlastingRecipe rawGoldRecipe = new BlastingRecipe(
                        new NamespacedKey(this, "raw_gold_blasting"),
                        new ItemStack(Material.GOLD_INGOT),
                        Material.RAW_GOLD,
                        1.0f,
                        100);
                getServer().addRecipe(rawGoldRecipe);
                count++;
            }

            // Recette pour RAW_COPPER -> COPPER_INGOT (Blast Furnace)
            if (Material.RAW_COPPER != null && Material.COPPER_INGOT != null) {
                BlastingRecipe rawCopperRecipe = new BlastingRecipe(
                        new NamespacedKey(this, "raw_copper_blasting"),
                        new ItemStack(Material.COPPER_INGOT),
                        Material.RAW_COPPER,
                        0.7f,
                        100);
                getServer().addRecipe(rawCopperRecipe);
                count++;
            }

            // Recette pour ANCIENT_DEBRIS -> NETHERITE_SCRAP (Blast Furnace)
            if (Material.ANCIENT_DEBRIS != null && Material.NETHERITE_SCRAP != null) {
                BlastingRecipe ancientDebrisRecipe = new BlastingRecipe(
                        new NamespacedKey(this, "ancient_debris_blasting"),
                        new ItemStack(Material.NETHERITE_SCRAP),
                        Material.ANCIENT_DEBRIS,
                        2.0f,
                        100);
                getServer().addRecipe(ancientDebrisRecipe);
                count++;
            }

            if (count > 0) {
                logInfo("&a✓ " + count + " recettes de cuisson ont été enregistrées");
            }
        } catch (Exception e) {
            logError("Erreur lors de l'enregistrement des recettes de cuisson");
            e.printStackTrace();
        }
    }

    public static EgoriaMC getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public WarpListGuiManager getWarpListGuiManager() {
        return warpListGuiManager;
    }

    public AutoMessageManager getAutoMessageManager() {
        return autoMessageManager;
    }

    public SpawnConfigManager getSpawnConfigManager() {
        return spawnConfigManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public WarnManager getWarnManager() {
        return warnManager;
    }

    public BackpackManager getBackpackManager() {
        return backpackManager;
    }

    public BackpackInventoryManager getBackpackInventoryManager() {
        return backpackInventoryManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public HelpGuiManager getHelpGuiManager() {
        return helpGuiManager;
    }

    public BalanceTopGuiManager getBalanceTopGuiManager() {
        return balanceTopGuiManager;
    }
}
