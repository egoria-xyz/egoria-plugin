package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gestionnaire de connexion BDD (fichier database.yml dans le dossier du plugin, non poussé sur Git).
 */
public class DatabaseManager {

    private final EgoriaMC plugin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "EgoriaMC-Database");
        t.setDaemon(true);
        return t;
    });
    private File databaseFile;
    private String jdbcUrl;
    private String user;
    private String password;
    private boolean configured;

    public DatabaseManager(EgoriaMC plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Charge ou crée database.yml (copie depuis database.default.yml si absent).
     */
    public void loadConfig() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        databaseFile = new File(dataFolder, "database.yml");
        if (!databaseFile.exists()) {
            try (var in = plugin.getResource("database.default.yml")) {
                if (in != null) {
                    java.nio.file.Files.copy(in, databaseFile.toPath());
                }
            } catch (Exception e) {
                plugin.logError("Impossible de créer database.yml : " + e.getMessage());
            }
        }
        if (!databaseFile.exists()) {
            plugin.logError("Fichier database.yml introuvable. Créez-le à partir de database.default.yml.");
            configured = false;
            return;
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(databaseFile);
        String host = cfg.getString("host", "localhost");
        int port = cfg.getInt("port", 3306);
        String database = cfg.getString("database", "egoriamc");
        user = cfg.getString("user", "");
        password = cfg.getString("password", "");
        jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
        configured = !user.isEmpty();
        if (!configured) {
            plugin.logError("database.yml : renseignez user et password.");
        }
    }

    public boolean isConfigured() {
        return configured;
    }

    /**
     * Obtient une connexion (à fermer par l'appelant ou via try-with-resources).
     */
    public Connection getConnection() throws SQLException {
        if (!configured) {
            throw new SQLException("Base de données non configurée (database.yml)");
        }
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    /**
     * Exécute une tâche BDD de façon asynchrone (pour ne pas bloquer le thread principal).
     */
    public void runAsync(Runnable task) {
        executor.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                plugin.logError("Erreur BDD asynchrone: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
