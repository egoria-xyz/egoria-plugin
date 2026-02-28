package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TabCompleter pour la commande /home
 */
public class HomeCommandTabCompleter implements TabCompleter {

    private final HomeManager homeManager;

    public HomeCommandTabCompleter(EgoriaMC plugin) {
        this.homeManager = plugin.getHomeManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        // Premier argument : sous-commandes ou noms de homes
        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // Ajouter les sous-commandes basiques
            completions.add("set");
            completions.add("delete");
            completions.add("list");
            completions.add("gui");

            // Ajouter les homes du joueur
            Map<String, Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());
            completions.addAll(playerHomes.keySet());

            // Ajouter les sous-commandes d'administration
            if (player.hasPermission("egoriamc.home.staff")) {
                completions.add("staffhomes");
            }

            // Filtrer par ce qui a été tapé
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        // Deuxième argument selon la sous-commande
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("delete")) {
                String input = args[1].toLowerCase();
                // Suggérer les noms de homes du joueur
                Map<String, Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());
                return playerHomes.keySet().stream()
                        .filter(s -> s.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            } else if (subCommand.equals("staffhomes") && player.hasPermission("egoriamc.home.staff")) {
                String input = args[1].toLowerCase();
                // Suggérer les noms de joueurs en ligne
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(s -> s.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        }

        // Troisième argument pour staffhomes
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("staffhomes") && player.hasPermission("egoriamc.home.staff")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    String input = args[2].toLowerCase();
                    Map<String, Location> targetHomes = homeManager.getPlayerHomes(target.getUniqueId());
                    return targetHomes.keySet().stream()
                            .filter(s -> s.toLowerCase().startsWith(input))
                            .collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();
    }
}
