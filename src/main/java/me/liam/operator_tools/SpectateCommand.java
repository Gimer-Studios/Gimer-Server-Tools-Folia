package me.liam.operator_tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("GimerServerTools.spectate")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                spectatePlayer(player, target);
                player.sendMessage(ChatColor.GREEN + "You are now spectating " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Player not found!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /spectate <player>");
        }

        return true;
    }


    private void spectatePlayer(Player spectator, Player target) {
        spectator.setGameMode(GameMode.SPECTATOR);
        spectator.teleport(target);
    }
}