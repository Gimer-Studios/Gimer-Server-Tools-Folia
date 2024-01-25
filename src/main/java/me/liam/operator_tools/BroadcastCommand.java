package me.liam.operator_tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadcastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("broadcast")) {
            if (sender.isOp()) {
                if (args.length > 0) {
                    String message = ChatColor.YELLOW + "[Broadcast] " + ChatColor.WHITE + String.join(" ", args);
                    Bukkit.broadcastMessage(message);
                    playPingSound();
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /broadcast <message>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        }
        return false;
    }

    private void playPingSound() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        }
    }
}
