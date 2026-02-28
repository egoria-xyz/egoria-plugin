package me.egoriamc.manager;

import me.egoriamc.EgoriaMC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de la GUI du top argent
 */
public class BalanceTopGuiManager {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;
    private final EconomyManager economyManager;

    private static final int INVENTORY_SIZE = 27;
    private static final int ITEMS_PER_PAGE = 25; // 25 items + 2 boutons de navigation

    public BalanceTopGuiManager(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
    }

    /**
     * Ouvre la GUI du top argent
     */
    public void openBalanceTopGui(Player player, int page) {
        if (!economyManager.isEnabled()) {
            player.sendMessage(messageManager.getMessage("errors.economy-disabled"));
            return;
        }

        Inventory gui = createBalanceTopInventory(page);
        player.openInventory(gui);
    }

    /**
     * Crée l'inventaire du top argent (static pour accès depuis le listener)
     */
    public static Inventory createBalanceTopInventory(int page) {
        // Récupérer tous les joueurs (connectés + offline) et les trier par solde
        // décroissant
        List<PlayerBalance> players = new ArrayList<>();
        EconomyManager economyManager = EgoriaMC.getInstance().getEconomyManager();
        MessageManager messageManager = EgoriaMC.getInstance().getMessageManager();

        // Récupérer tous les joueurs offline ET online
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            try {
                if (offlinePlayer.getName() != null) { // Vérifier que le joueur a un nom valide
                    // Essayer de récupérer la balance comme joueur online si c'est possible
                    Player onlinePlayer = offlinePlayer.getPlayer();
                    double balance;

                    if (onlinePlayer != null) {
                        // Le joueur est connecté
                        balance = economyManager.getBalance(onlinePlayer);
                        players.add(new PlayerBalance(onlinePlayer, balance));
                    } else {
                        // Le joueur est offline, créer une entrée offline
                        // Note: Nous créons une instance fictive pour l'affichage
                        balance = economyManager.getBalance(offlinePlayer);
                        players.add(new PlayerBalance(offlinePlayer, balance, offlinePlayer.getName()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Log pour debug
        EgoriaMC.getInstance().logInfo("&eBalTop: " + players.size() + " joueurs trouvés (online + offline)");

        // Trier du plus riche au plus pauvre
        players.sort((a, b) -> Double.compare(b.balance, a.balance));

        // Calculer le nombre total de pages
        int totalPages = (int) Math.ceil((double) players.size() / ITEMS_PER_PAGE);
        if (page > totalPages)
            page = totalPages;
        if (page < 1)
            page = 1;

        // Créer le titre avec le numéro de page
        String title = "§e💰 Top Argent (" + page + "/" + totalPages + ")";
        Inventory gui = Bukkit.createInventory(null, INVENTORY_SIZE, title);

        // Calculer les indices de début et fin
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, players.size());

        // Ajouter les items du classement
        int slot = 0;
        int rank = startIndex + 1;

        for (int i = startIndex; i < endIndex && slot < 25; i++) {
            try {
                PlayerBalance pb = players.get(i);
                // Créer l'item avec la couleur du grade
                ItemStack item = createRankItem(EgoriaMC.getInstance(), rank, pb, i + 1);
                gui.setItem(slot, item);

                slot++;
                rank++;
            } catch (Exception e) {
                EgoriaMC.getInstance().logError("&eErreur lors de la création d'un item baltop: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Ajouter les boutons de navigation
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
     * Crée un item de navigation
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
     * Crée un item pour un joueur du classement
     */
    private static ItemStack createRankItem(EgoriaMC plugin, int rank, PlayerBalance pb, int index) {
        MessageManager messageManager = plugin.getMessageManager();
        EconomyManager economyManager = plugin.getEconomyManager();

        // Déterminer la couleur selon le rang
        Material material;
        String rankColor;

        if (rank == 1) {
            material = Material.GOLD_BLOCK;
            rankColor = "&6";
        } else if (rank == 2) {
            material = Material.IRON_BLOCK;
            rankColor = "&7";
        } else if (rank == 3) {
            material = Material.COPPER_BLOCK;
            rankColor = "&c";
        } else {
            material = Material.STONE;
            rankColor = "&8";
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Déterminer la médaille
            String medal = switch (rank) {
                case 1 -> "🥇";
                case 2 -> "🥈";
                case 3 -> "🥉";
                default -> "#" + rank;
            };

            String playerName = pb.playerName;
            String grade;
            if (pb.isOnline && pb.player != null) {
                grade = messageManager.getPlayerGroup(pb.player);
            } else {
                grade = "member"; // Les joueurs offline ont le grade par défaut
            }
            String gradeColor = getGradeColor(grade);
            String gradeDisplay = gradeColor + capitalize(grade) + (pb.isOnline ? "" : " &7(Offline)");

            // Debug: afficher le grade détecté
            EgoriaMC.getInstance().logInfo("&7[BalTop] Joueur: " + playerName + " | Grade: " + grade);

            String balanceFormatted = economyManager.formatBalance(pb.balance);

            meta.setDisplayName(messageManager.translateColors(rankColor + medal + " " + playerName));

            List<String> lore = new ArrayList<>();
            lore.add(messageManager.translateColors("&7Grade: &f" + gradeDisplay));
            lore.add(messageManager.translateColors("&7Solde: &e" + balanceFormatted));

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Retourne la couleur du grade
     */
    private static String getGradeColor(String grade) {
        return switch (grade.toLowerCase()) {
            case "owner" -> "&4";
            case "admin" -> "&c";
            case "developer" -> "&9";
            case "staff" -> "&5";
            case "vip" -> "&6";
            default -> "&7";
        };
    }

    /**
     * Capitalise une chaîne
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Classe interne pour stocker les données des joueurs
     */
    public static class PlayerBalance {
        public Player player;
        public OfflinePlayer offlinePlayer;
        public double balance;
        public String playerName;
        public boolean isOnline;

        // Constructeur pour joueur online
        public PlayerBalance(Player player, double balance) {
            this.player = player;
            this.offlinePlayer = player;
            this.balance = balance;
            this.playerName = player.getName();
            this.isOnline = true;
        }

        // Constructeur pour joueur offline
        public PlayerBalance(OfflinePlayer offlinePlayer, double balance, String playerName) {
            this.player = null;
            this.offlinePlayer = offlinePlayer;
            this.balance = balance;
            this.playerName = playerName;
            this.isOnline = false;
        }
    }
}
