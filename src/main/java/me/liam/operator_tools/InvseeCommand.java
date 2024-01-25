package me.liam.operator_tools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InvseeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player operator = (Player) sender;


            if (operator.hasPermission("operator_tools.invsee")) {
                if (args.length == 1) {
                    String targetPlayerName = args[0];
                    Player targetPlayer = operator.getServer().getPlayer(targetPlayerName);

                    if (targetPlayer != null) {
                        operator.openInventory(targetPlayer.getInventory());


                        operator.getOpenInventory().getTopInventory().setContents(targetPlayer.getInventory().getContents());

                        operator.getOpenInventory().getTopInventory().setItem(36, targetPlayer.getInventory().getHelmet());
                        operator.getOpenInventory().getTopInventory().setItem(37, targetPlayer.getInventory().getChestplate());
                        operator.getOpenInventory().getTopInventory().setItem(38, targetPlayer.getInventory().getLeggings());
                        operator.getOpenInventory().getTopInventory().setItem(39, targetPlayer.getInventory().getBoots());
                    } else {
                        operator.sendMessage("Player not found or not online.");
                    }
                } else {
                    operator.sendMessage("Usage: /invsee <player>");
                }
            } else {
                operator.sendMessage("You do not have permission to use this command!");
            }
        } else {
            sender.sendMessage("Only players can use this command!");
        }

        return true;
    }
}