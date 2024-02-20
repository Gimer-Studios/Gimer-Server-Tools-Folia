package me.liam.operator_tools;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class JoinMessages implements Listener {
    private String[] joinMessages = {
            "Welcome %player%, enjoy your stay!",
            "Hey %player%, nice to see you!",
            "%player% has joined the game!",
            "Look who's here! It's %player%!",
            "Welcome, %player%. Stay awhile and listen.",
            "Welcome, %player%. We were expecting you ( ͡° ͜ʖ ͡°)",
            "Welcome, %player%. We hope you brought pizza.",
            "Welcome %player%. Leave your weapons by the door.",
            "A wild %player% appeared.",
            "Swoooosh. %player% just landed.",
            "Brace yourselves. %player% just joined the server.",
            "%player% just joined. Hide your bananas.",
            "%player% just arrived. Seems OP - please nerf.",
            "%player% just slid into the server.",
            "A %player% has spawned in the server.",
            "Big %player% showed up!",
            "Where’s %player%? In the server!",
            "%player% hopped into the server. Kangaroo!!",
            "%player% just showed up. Hold my beer."
            // Discord is canon???
    };

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        String randomMessage = getRandomJoinMessage(playerName);
        event.setJoinMessage(ChatColor.GREEN + randomMessage);
    }

    private String getRandomJoinMessage(String playerName) {
        String randomMessage = joinMessages[new Random().nextInt(joinMessages.length)];
        return randomMessage.replace("%player%", playerName);
    }
}