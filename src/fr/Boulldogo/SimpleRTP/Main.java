package fr.Boulldogo.SimpleRTP;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements CommandExecutor {
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public boolean isOnCooldown(Player player) {
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player)) {
            long lastTeleportTime = cooldowns.get(player);
            long cooldownTime = getConfig().getInt("randomtp-cooldown") * 1000;
            return now - lastTeleportTime < cooldownTime;
        }
        return false;
    }

    public void setCooldown(Player player) {
        long now = System.currentTimeMillis();
        cooldowns.put(player, now);
    }

    public long getRemainingCooldown(Player player) {
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player)) {
            long lastTeleportTime = cooldowns.get(player);
            long cooldownTime = getConfig().getInt("randomtp-cooldown") * 1000; 
            long remainingCooldown = cooldownTime - (now - lastTeleportTime);
            return Math.max(0, remainingCooldown / 1000); 
        }
        return 0;
    }

    @Override
    public void onEnable() {

        String version = getConfig().getString("version");

        getLogger().info("§aLe plugin SimpleRTP version " + version + " by Boulldogo a été chargé avec succès.");

        saveDefaultConfig();

        this.getCommand("rtp").setExecutor(new RandomTeleportCommand(this));
        VersionChecker.checkVersion(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("§cLe plugin SimpleRTP a été désactivé.");
    }
}
