package me.liam.operator_tools;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatLogger implements Listener {

    private final JavaPlugin plugin;
    private static final String LOG_FILE_PATH = "plugins/GimerServerTools/ChatLogs/chat.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ChatLogger(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        File chatLogsFolder = new File("plugins/GimerServerTools/ChatLogs");
        if (!chatLogsFolder.exists()) {
            chatLogsFolder.mkdirs();
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();

        logChatMessage(playerName, message);
    }

    private void logChatMessage(String playerName, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            String formattedMessage = "[" + DATE_FORMAT.format(new Date()) + "] " + playerName + ": " + message;
            System.out.println(formattedMessage);
            writer.println(formattedMessage);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to log chat message: " + e.getMessage());
        }
    }
}