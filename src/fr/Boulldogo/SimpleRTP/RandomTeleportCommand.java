package fr.Boulldogo.SimpleRTP;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;

public class RandomTeleportCommand implements CommandExecutor {
    private final Main plugin;
    private final Economy economy;

    public RandomTeleportCommand(Main plugin) {
        this.plugin = plugin;
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
        } else {
            this.economy = null;
            plugin.getLogger().warning("Vault (économie) n'a pas été trouvé. Le coût du RandomTP ne sera donc pas pris en compte et ne fonctionnera pas.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande est réservée aux joueurs.");
            return true;
        }

        Player player = (Player) sender;
        double cost = 0.0;

        boolean isRandomTpCooldownEnabled = plugin.getConfig().getBoolean("use-cooldown");
        boolean isRandomTpCostEnabled = plugin.getConfig().getBoolean("vault-support");

        if (isRandomTpCooldownEnabled && plugin.isOnCooldown(player)) {
            long cooldownTime = plugin.getRemainingCooldown(player);
            String cooldownMessage = plugin.getConfig().getString("cooldown-message").replace("{cooldown}", String.valueOf(cooldownTime));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', cooldownMessage));
            return true;
        }

        if (isRandomTpCostEnabled && economy != null) {
            cost = plugin.getConfig().getDouble("cost-per-teleport");
            if (economy.getBalance(player) < cost) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-money-message").replace("{cost}", String.valueOf(cost))));
                return true;
            }
            economy.withdrawPlayer(player, cost);
        }

        int minX = plugin.getConfig().getInt("minimum-coords-teleport.x");
        int minZ = plugin.getConfig().getInt("minimum-coords-teleport.z");
        int maxX = plugin.getConfig().getInt("maximum-coords-teleport.x");
        int maxZ = plugin.getConfig().getInt("maximum-coords-teleport.z");

        int randomX = minX + (int) (Math.random() * (maxX - minX + 1));
        int randomZ = minZ + (int) (Math.random() * (maxZ - minZ + 1));

        int randomY = player.getWorld().getHighestBlockYAt(randomX, randomZ);

        player.teleport(new Location(player.getWorld(), randomX, randomY, randomZ));

        if (isRandomTpCooldownEnabled) {
            plugin.setCooldown(player);
        }

        String message = plugin.getConfig().getString("teleport-message")
                .replace("{coords}", "(" + randomX + ", " + randomY + ", " + randomZ + ")")
                .replace("{price}", String.valueOf(cost));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        return true;
    }
}
