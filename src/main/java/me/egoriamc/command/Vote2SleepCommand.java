package me.egoriamc.command;

import me.egoriamc.EgoriaMC;
import me.egoriamc.manager.MessageManager;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Commande /vote2sleep - Permet aux joueurs de voter pour passer la nuit
 */
public class Vote2SleepCommand implements CommandExecutor {

    private final EgoriaMC plugin;
    private final MessageManager messageManager;
    private static final Map<World, Set<String>> WORLD_VOTES = new HashMap<>();
    private static final long COOLDOWN_MS = 60000; // 1 minute
    private static final Map<World, Long> WORLD_COOLDOWN = new HashMap<>();

    public Vote2SleepCommand(EgoriaMC plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vérifier si le sender est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("vote2sleep.must-be-player"));
            return true;
        }

        Player player = (Player) sender;
        World world = player.getWorld();

        // Vérifier si c'est la nuit
        long time = world.getTime();
        if (time >= 0 && time < 12541) { // Entre 0 et le coucher du soleil (approximativement)
            player.sendMessage(messageManager.getMessage("vote2sleep.not-night"));
            return true;
        }

        // Vérifier le cooldown
        if (WORLD_COOLDOWN.containsKey(world)) {
            long lastVote = WORLD_COOLDOWN.get(world);
            long now = System.currentTimeMillis();
            if (now - lastVote < COOLDOWN_MS) {
                long remaining = (COOLDOWN_MS - (now - lastVote)) / 1000;
                player.sendMessage(messageManager.getMessage("vote2sleep.cooldown", (int) remaining));
                return true;
            }
        }

        // Initialiser la liste de votes si nécessaire
        WORLD_VOTES.putIfAbsent(world, new HashSet<>());
        Set<String> votes = WORLD_VOTES.get(world);

        // Vérifier si le joueur a déjà voté
        if (votes.contains(player.getName())) {
            player.sendMessage(messageManager.getMessage("vote2sleep.already-voted"));
            return true;
        }

        // Ajouter le vote
        votes.add(player.getName());

        // Compter le nombre de joueurs en ligne
        int onlinePlayers = world.getPlayers().size();
        int requiredVotes = Math.max(1, (int) Math.ceil(onlinePlayers * 0.5)); // 50% des joueurs

        // Afficher le message de vote
        int currentVotes = votes.size();
        player.sendMessage(messageManager.getMessage("vote2sleep.vote-added", currentVotes, requiredVotes));

        // Diffuser le message à tous les joueurs du monde
        String broadcastMessage = messageManager.getMessage("vote2sleep.vote-broadcast",
                player.getName(), currentVotes, requiredVotes);
        for (Player p : world.getPlayers()) {
            p.sendMessage(broadcastMessage);
        }

        // Jouer un son
        playSound(world);

        // Vérifier si on a assez de votes
        if (currentVotes >= requiredVotes) {
            skipNight(world);
            WORLD_VOTES.remove(world);
            WORLD_COOLDOWN.put(world, System.currentTimeMillis());
        }

        return true;
    }

    /**
     * Passe la nuit
     */
    private void skipNight(World world) {
        world.setTime(0); // Définir l'heure à 6h du matin

        // Message de succès
        String successMessage = messageManager.getMessage("vote2sleep.night-skipped");
        for (Player player : world.getPlayers()) {
            player.sendMessage(successMessage);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        }

        // Log dans la console
        plugin.logInfo("La nuit a été passée dans le monde &e" + world.getName() + "&e !");
    }

    /**
     * Joue un son à tous les joueurs
     */
    private void playSound(World world) {
        for (Player player : world.getPlayers()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.0f);
        }
    }

    /**
     * Réinitialise les votes du monde (appelé par un timer ou événement)
     */
    public static void resetWorldVotes(World world) {
        WORLD_VOTES.remove(world);
    }
}
