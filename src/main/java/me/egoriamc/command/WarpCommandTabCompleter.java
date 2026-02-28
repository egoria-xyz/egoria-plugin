package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.WarpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TabCompleter pour la commande /warp
 */
public class WarpCommandTabCompleter implements TabCompleter {

    private final WarpManager warpManager;

    public WarpCommandTabCompleter(EgoriaMC plugin) {
        this.warpManager = plugin.getWarpManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        // Premier argument : sous-commandes ou noms de warps
        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // Ajouter les sous-commandes d'administration
            if (player.hasPermission("egoriamc.warp.admin")) {
                completions.add("set");
                completions.add("delete");
                completions.add("list");
                completions.add("info");
            }

            // Ajouter tous les noms de warps
            completions.addAll(warpManager.getAllWarps().keySet());

            // Filtrer par ce qui a été tapé
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        // Deuxième argument selon la sous-commande
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("delete") || subCommand.equals("info")) {
                String input = args[1].toLowerCase();
                // Suggérer les noms de warps existants
                return warpManager.getAllWarps().keySet().stream()
                        .filter(s -> s.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }
}
