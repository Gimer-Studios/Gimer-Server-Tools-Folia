package me.liam.operator_tools;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

public class KillAllHostileCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public KillAllHostileCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("operatortools.killallhostile") || sender.isOp())) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (isHostile(livingEntity)) {
                        livingEntity.remove();
                    }
                }
            }
        }

        sender.sendMessage("All hostile mobs killed.");
        return true;
    }

    private boolean isHostile(LivingEntity entity) {
        // Add more hostile mob types if needed
        return entity instanceof Blaze || entity instanceof Creeper || entity instanceof Ghast ||
                entity instanceof Phantom || entity instanceof Silverfish || entity instanceof Skeleton ||
                entity instanceof Slime || entity instanceof Witch || entity instanceof Warden ||
                entity instanceof Stray || entity instanceof Drowned ||
                entity instanceof Ravager || entity instanceof Guardian || entity instanceof Endermite ||
                entity instanceof Pillager || entity instanceof Spider;
    }
}
