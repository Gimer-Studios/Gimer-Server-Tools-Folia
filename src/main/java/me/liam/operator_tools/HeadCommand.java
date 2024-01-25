package me.liam.operator_tools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("operator_tools.head")) {
                if (args.length == 1) {
                    String playerName = args[0];

                    ItemStack playerHead = createPlayerHead(playerName);


                    player.getInventory().addItem(playerHead);
                    player.sendMessage("Received the head of " + playerName);
                } else {
                    player.sendMessage("Usage: /head <player>");
                }
            } else {
                player.sendMessage("You do not have permission to use this command!");
            }
        } else {
            sender.sendMessage("Only players can use this command!");
        }

        return true;
    }

    private ItemStack createPlayerHead(String playerName) {
        ItemStack head = new ItemStack(org.bukkit.Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwner(playerName);

        head.setItemMeta(meta);

        return head;
    }
}
