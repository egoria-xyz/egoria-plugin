package me.egoriamc.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Gestionnaire d'économie Vault
 */
public class EconomyManager {

    private final JavaPlugin plugin;
    private Economy economy;
    private boolean enabled;
    private static final double MAX_BALANCE = 50000000; // Limite de 50 million d'argent

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabled = false;
        setupEconomy();
    }

    /**
     * Configure l'économie Vault
     */
    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger()
                    .warning("Vault n'est pas installé ! Les fonctionnalités économiques seront désactivées.");
            this.enabled = false;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("Aucun plugin d'économie compatible Vault n'est installé.");
            this.enabled = false;
            return;
        }

        this.economy = rsp.getProvider();
        this.enabled = true;
        plugin.getLogger().info("✓ Économie Vault activée avec " + economy.getName());
    }

    /**
     * Retourne si l'économie est activée
     */
    public boolean isEnabled() {
        return enabled && economy != null;
    }

    /**
     * Récupère le solde d'un joueur
     */
    public double getBalance(Player player) {
        if (!isEnabled())
            return 0;
        return economy.getBalance(player);
    }

    /**
     * Retorne le nom de la devise (singulier)
     */
    public String getCurrencyNameSingular() {
        if (!isEnabled())
            return "$";
        return economy.currencyNameSingular();
    }

    /**
     * Retourne le nom de la devise (pluriel)
     */
    public String getCurrencyNamePlural() {
        if (!isEnabled())
            return "$";
        return economy.currencyNamePlural();
    }

    /**
     * Retourne le format du solde avec devise
     */
    public String formatBalance(double balance) {
        if (!isEnabled())
            return balance + " $";
        return economy.format(balance);
    }

    /**
     * Ajoute de l'argent à un joueur (avec limite de 1 000 000)
     */
    public boolean addBalance(Player player, double amount) {
        if (!isEnabled())
            return false;

        double currentBalance = economy.getBalance(player);
        double newBalance = currentBalance + amount;

        // Vérifier si la limite est dépassée
        if (newBalance > MAX_BALANCE) {
            return false; // Dépasse la limite
        }

        economy.depositPlayer(player, amount);
        return true;
    }

    /**
     * Retire de l'argent à un joueur
     */
    public boolean removeBalance(Player player, double amount) {
        if (!isEnabled())
            return false;
        economy.withdrawPlayer(player, amount);
        return true;
    }

    /**
     * Définit le solde d'un joueur (avec limite de 1 000 000)
     */
    public void setBalance(Player player, double amount) {
        if (!isEnabled())
            return;

        // Ne pas dépasser la limite
        double finalAmount = Math.min(amount, MAX_BALANCE);

        // Soustraire le solde actuel puis ajouter le nouveau
        double current = economy.getBalance(player);
        if (current < finalAmount) {
            economy.depositPlayer(player, finalAmount - current);
        } else {
            economy.withdrawPlayer(player, current - finalAmount);
        }
    }

    /**
     * Retourne l'instance de Economy
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * Retourne la limite maximale d'argent
     */
    public double getMaxBalance() {
        return MAX_BALANCE;
    }
}
