package me.liam.operator_tools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlaytimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /playertime <player>");
            return true;
        }

        String playerName = args[0];
        Player targetPlayer = sender.getServer().getPlayer(playerName);

        if (targetPlayer != null) {
            long playtimeMillis = System.currentTimeMillis() - targetPlayer.getFirstPlayed();
            long playtimeHours = playtimeMillis / (1000 * 60 * 60);
            String lastPlayedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(targetPlayer.getLastPlayed()));

            sender.sendMessage("Playtime for " + playerName + ": " + playtimeHours + " hours");
            sender.sendMessage("Last played: " + lastPlayedDate);
        } else {
            sender.sendMessage("Player not found or not online.");
        }

        return true;
    }
}