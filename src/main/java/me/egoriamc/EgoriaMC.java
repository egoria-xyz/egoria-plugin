package me.egoriamc;

import me.egoriamc.command.AnnounceCommand;
import me.egoriamc.command.HomeCommand;
import me.egoriamc.command.HelpCommand;
import me.egoriamc.command.PluginsCommand;
import me.egoriamc.command.WarpCommand;
import me.egoriamc.listener.ChatListener;
import me.egoriamc.listener.PlayerEventListener;
import me.egoriamc.listener.MentionListener;
import me.egoriamc.manager.ConfigManager;
import me.egoriamc.manager.HomeManager;
import me.egoriamc.manager.MessageManager;
import me.egoriamc.manager.WarpManager;
import me.egoriamc.util.EmojiUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EgoriaMC extends JavaPlugin {

    private static EgoriaMC instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private HomeManager homeManager;
    private WarpManager warpManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            // Initialiser les gestionnaires
            this.configManager = new ConfigManager(this);
            this.messageManager = new MessageManager(this);
            this.homeManager = new HomeManager(this);
            this.warpManager = new WarpManager(this);

            // Charger les emojis depuis emojis.yml
            EmojiUtil.loadEmojis(this);

            // Enregistrer les commandes
            getCommand("home").setExecutor(new HomeCommand(this));
            getCommand("warp").setExecutor(new WarpCommand(this));
            getCommand("help").setExecutor(new HelpCommand(this));
            getCommand("plugins").setExecutor(new PluginsCommand());
            getCommand("annonce").setExecutor(new AnnounceCommand(this));

            // Enregistrer les événements
            getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
            getServer().getPluginManager().registerEvents(new MentionListener(this), this);
            getServer().getPluginManager().registerEvents(new ChatListener(this), this);

            logInfo("&ePlugin activé avec succès !");
            logInfo("&e- Gestion des homes : &aACTIVÉE");
            logInfo("&e- Gestion des warps : &aACTIVÉE");
            logInfo("&e- Gestion des messages : &aACTIVÉE");
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
}
