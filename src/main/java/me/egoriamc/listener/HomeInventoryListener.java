package me.egoriamc.listener;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.HomeManager;
import me.egoriamc.manager.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;

/**
 * Listener pour les interactions avec l'inventaire des homes
 */
public class HomeInventoryListener implements Listener {

    private final EgoriaMC plugin;
    private final HomeManager homeManager;
    private final MessageManager messageManager;

    public HomeInventoryListener(EgoriaMC plugin) {
        this.plugin = plugin;
        this.homeManager = plugin.getHomeManager();
        this.messageManager = plugin.getMessageManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Vérifier si c'est un inventaire de homes (titre contient "- Homes")
        if (event.getView().getTitle() == null || !event.getView().getTitle().contains("- Homes")) {
            return;
        }

        // Empêcher toute interaction avec l'inventaire
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();

        // Vérifier que l'item cliqué existe et a un nom d'affichage
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        // Récupérer le nom du home depuis le nom d'affichage (enlever les codes
        // couleur)
        String displayName = meta.getDisplayName();
        String homeName = messageManager.stripColors(displayName);

        // Extraire le nom du joueur du titre de l'inventaire (format: "nom - Homes")
        String title = event.getView().getTitle();
        int dashIndex = title.indexOf(" - Homes");
        if (dashIndex == -1) {
            return;
        }

        String targetName = messageManager.stripColors(title.substring(0, dashIndex));

        // Récupérer l'UUID du joueur cible
        // Chercher dans la map des homes chargés
        UUID targetUuid = findPlayerUuidByName(targetName);
        if (targetUuid == null) {
            player.sendMessage(messageManager.translateColors("₾ → &7Joueur non trouvé."));
            player.closeInventory();
            return;
        }

        // Récupérer la location du home
        Location home = homeManager.getHome(targetUuid, homeName);
        if (home == null) {
            player.sendMessage(messageManager.getHomeNotFound());
            player.closeInventory();
            return;
        }

        // Téléporter le joueur
        player.closeInventory();
        player.teleport(home);
        player.sendMessage(messageManager.getHomeTpSuccess(homeName));
        plugin.logInfo(messageManager
                .translateColors("₾ → &a" + player.getName() + " &es'est téléporté au home: &b" + homeName));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Vérifier si c'est un inventaire de homes
        if (event.getView().getTitle() == null || !event.getView().getTitle().contains("- Homes")) {
            return;
        }

        // Empêcher le drag and drop
        event.setCancelled(true);
    }

    /**
     * Trouve l'UUID d'un joueur par son nom dans la map des homes
     */
    private UUID findPlayerUuidByName(String playerName) {
        // Parcourir tous les joueurs en ligne
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(playerName)) {
                return onlinePlayer.getUniqueId();
            }
        }

        // Si le joueur n'est pas en ligne, essayer de le trouver dans les fichiers de
        // données
        // Pour l'instant, retourner null
        return null;
    }
}