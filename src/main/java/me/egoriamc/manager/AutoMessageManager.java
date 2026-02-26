package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gestionnaire des messages automatiques récurrents
 */
public class AutoMessageManager {

    private final EgoriaMC plugin;
    private FileConfiguration messageAutoConfig;
    private File messageAutoFile;

    private Map<String, Long> messageCooldowns;
    private int taskId = -1;
    private int messageIndex = 0;
    private List<String> messageKeys;

    public AutoMessageManager(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageCooldowns = new HashMap<>();
        this.messageKeys = new ArrayList<>();

        loadConfig();
        startAutoMessages();
    }

    /**
     * Charge le fichier de configuration MessageAuto.yml
     */
    private void loadConfig() {
        messageAutoFile = new File(plugin.getDataFolder(), "MessageAuto.yml");

        // Créer le fichier s'il n'existe pas
        if (!messageAutoFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                plugin.saveResource("MessageAuto.yml", false);
            } catch (Exception e) {
                plugin.logError("Erreur lors de la création de MessageAuto.yml");
                e.printStackTrace();
            }
        }

        messageAutoConfig = YamlConfiguration.loadConfiguration(messageAutoFile);

        // Charger les clés des messages
        ConfigurationSection messagesSection = messageAutoConfig.getConfigurationSection("messages");
        if (messagesSection != null) {
            messageKeys.addAll(messagesSection.getKeys(false));
        }

        plugin.logInfo("&a✓ Configuration MessageAuto chargée (" + messageKeys.size() + " messages)");
    }

    /**
     * Démarre la tâche d'envoi automatique des messages
     */
    private void startAutoMessages() {
        int globalInterval = messageAutoConfig.getInt("global-interval", 60);

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (messageKeys.isEmpty()) {
                return;
            }

            String mode = messageAutoConfig.getString("mode", "sequential");
            String messageKey;

            if ("random".equalsIgnoreCase(mode)) {
                // Mode aléatoire
                messageKey = messageKeys.get(new Random().nextInt(messageKeys.size()));
            } else {
                // Mode séquentiel (par défaut)
                messageKey = messageKeys.get(messageIndex);
                messageIndex = (messageIndex + 1) % messageKeys.size();
            }

            sendMessage(messageKey);
        }, globalInterval * 20L, globalInterval * 20L); // Convertir en ticks (1 sec = 20 ticks)
    }

    /**
     * Envoie un message à tous les joueurs
     */
    private void sendMessage(String messageKey) {
        ConfigurationSection messageSection = messageAutoConfig.getConfigurationSection("messages." + messageKey);

        if (messageSection == null) {
            return;
        }

        String message;
        if (messageSection.isList("message")) {
            // message est une liste de lignes (YAML avec - "ligne1", - "ligne2", ...)
            List<String> lines = messageSection.getStringList("message");
            message = String.join("", lines);
        } else {
            message = messageSection.getString("message", "");
        }
        long cooldown = messageSection.getLong("cooldown", 0);

        // Vérifier le cooldown
        long lastSent = messageCooldowns.getOrDefault(messageKey, 0L);
        long now = System.currentTimeMillis();

        if (now - lastSent < (cooldown * 1000)) {
            // Cooldown non écoulé, sauter ce message
            return;
        }

        // Mettre à jour le cooldown
        messageCooldowns.put(messageKey, now);

        // Traduire les codes de couleur et envoyer le message
        String translatedMessage = translateHexColorCodes(message);
        Bukkit.broadcastMessage(translatedMessage);
    }

    /**
     * Traduit les codes de couleur
     */
    private String translateHexColorCodes(String message) {
        return message.replace("&", "§");
    }

    /**
     * Recharge la configuration
     */
    public void reloadConfig() {
        Bukkit.getScheduler().cancelTask(taskId);
        messageCooldowns.clear();
        messageIndex = 0;
        messageKeys.clear();

        loadConfig();
        startAutoMessages();
        plugin.logInfo("&a✓ Configuration MessageAuto rechargée");
    }

    /**
     * Arrête la tâche des messages automatiques
     */
    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    /**
     * Ajoute un nouveau message (en mémoire et en fichier)
     */
    public void addMessage(String key, String message, long cooldown) {
        messageAutoConfig.set("messages." + key + ".message", message);
        messageAutoConfig.set("messages." + key + ".cooldown", cooldown);

        try {
            messageAutoConfig.save(messageAutoFile);
            messageKeys.add(key);
            plugin.logInfo("&a✓ Message '" + key + "' ajouté");
        } catch (IOException e) {
            plugin.logError("Erreur lors de la sauvegarde du message");
            e.printStackTrace();
        }
    }

    /**
     * Supprime un message
     */
    public void removeMessage(String key) {
        messageAutoConfig.set("messages." + key, null);

        try {
            messageAutoConfig.save(messageAutoFile);
            messageKeys.remove(key);
            messageCooldowns.remove(key);
            plugin.logInfo("&a✓ Message '" + key + "' supprimé");
        } catch (IOException e) {
            plugin.logError("Erreur lors de la suppression du message");
            e.printStackTrace();
        }
    }

    /**
     * Obtient la liste de tous les messages
     */
    public List<String> getMessageKeys() {
        return new ArrayList<>(messageKeys);
    }

    /**
     * Obtient la configuration des messages automatiques
     */
    public FileConfiguration getConfig() {
        return messageAutoConfig;
    }
}
