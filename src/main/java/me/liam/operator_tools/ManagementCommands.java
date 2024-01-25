package me.liam.operator_tools;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.apache.logging.log4j.LogManager.getLogger;

public class ManagementCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !((Player) sender).isOp()) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("shutdown")) {
            sender.sendMessage("Shutting down the server...");
            getLogger().info("Server is now shutting down please wait");
            shutdownServer();
            return true;
        }
        return false;
    }


    private void shutdownServer() {
        // Shutdown the server
        Bukkit.getServer().shutdown();
    }
}
