package me.liam.operator_tools;

import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class OperatorTools extends JavaPlugin implements CommandExecutor, Listener {


    private final String versionInfoUrl = "https://gimer-studios.github.io/Gimer-Studios/version.txt";
    private UpdateChecker updateChecker;

    private static final Set<String> maintenanceKickedPlayers = new HashSet<>();
    private static final String BACKUP_FOLDER_NAME = "world_backups";

    private FileConfiguration config;
    private boolean maintenanceMode = false;
    private Set<String> mutedPlayers = new HashSet<>();

    private Map<Player, Player> spectators = new HashMap<>();
    private long serverStartTime;

    private ChatLogger chatLogger;



    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static class PlayerStats {
        private int kills;
        private int deaths;

        public int getKills() {
            return kills;
        }

        public void setKills(int kills) {
            this.kills = kills;
        }

        public int getDeaths() {
            return deaths;
        }

        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }
    }

    private final DecimalFormat coordinateFormat = new DecimalFormat("#.#");

    @Override
    public void onEnable() {
        getLogger().info("GimerServerTools Folia BETA plugin has been enabled!");
        getLogger().info("This is the Folia version of the plugin. If you are running this on another server software I recommend switching to that version.");
        int pluginId = 20270;
        new Metrics(this, pluginId);
        serverStartTime = System.currentTimeMillis();
        getLogger().info("Thanks for using Gimer Server Tools Folia BETA!");
        getCommand("c").setExecutor(this);
        getCommand("s").setExecutor(this);
        getCommand("a").setExecutor(this);
        getCommand("sp").setExecutor(this);
        getCommand("maintenance").setExecutor(this);
        getCommand("maintenancekick").setExecutor(this);
        getCommand("unmaintenance").setExecutor(this);
        getCommand("serverinfo").setExecutor(this);
        getCommand("playerstats").setExecutor(this);
        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("gcoords").setExecutor(this);
        getCommand("scoords").setExecutor(this);
        getCommand("spectate").setExecutor(new SpectateCommand());
        getCommand("mute").setExecutor(this);
        getCommand("unmute").setExecutor(this);
        getCommand("backup").setExecutor(this);
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("head").setExecutor(new HeadCommand());
        getCommand("playertime").setExecutor(new PlaytimeCommand());
        getCommand("killallhostile").setExecutor(new KillAllHostileCommand(this));
        getCommand("shutdown").setExecutor(new ManagementCommands());
        String version = Bukkit.getVersion();
        boolean isFolia = version.contains("Folia");
        chatLogger = new ChatLogger(this);
        updateChecker = new UpdateChecker(this, versionInfoUrl);
        updateChecker.checkForUpdates();
        Bukkit.getPluginManager().registerEvents(this, this);
    }




    @Override
    public void onDisable() {
        getLogger().info("Thanks for using Gimer Server Tools. It means a lot to me.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mute")) {
            if (sender.isOp()) {
                if (args.length == 1) {
                    String targetPlayer = args[0];
                    mutePlayer(targetPlayer);
                    sender.sendMessage(ChatColor.GREEN + "Player " + targetPlayer + " has been muted.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /mute <player>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("unmute")) {
            if (sender.isOp()) {
                if (args.length == 1) {
                    String targetPlayer = args[0];
                    unmutePlayer(targetPlayer);
                    sender.sendMessage(ChatColor.GREEN + "Player " + targetPlayer + " has been unmuted.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /unmute <player>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("maintenance")) {
            if (sender.isOp()) {
                maintenanceMode = true;
                broadcastMaintenanceMessage();
                playDingSound();
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("maintenancekick")) {
            if (sender.isOp()) {
                kickNonOpPlayers();
                maintenanceMode = true;
                sender.sendMessage(ChatColor.GREEN + "Maintenance kick executed successfully.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("backup")) {
            if (sender.isOp()) {
                boolean success = backupWorld();
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + "World backup completed successfully!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to create a world backup.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("unmaintenance")) {
            if (sender.isOp()) {
                maintenanceKickedPlayers.clear();
                maintenanceMode = false;
                sender.sendMessage(ChatColor.GREEN + "Maintenance mode lifted. Players can join back.");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("serverinfo")) {
            if (sender.hasPermission("operator_tools.commands")) {
                displayServerInfo(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("playerstats")) {
            if (sender instanceof Player) {
                Player target = (args.length > 0) ? Bukkit.getPlayer(args[0]) : (Player) sender;
                if (target != null) {
                    sender.sendMessage(ChatColor.BLUE + "----- Player Stats -----");
                    sender.sendMessage(ChatColor.GREEN + "Player: " + target.getName());
                    sender.sendMessage(ChatColor.GREEN + "Online: " + (target.isOnline() ? "Yes" : "No"));
                    sender.sendMessage(ChatColor.BLUE + "-------------------------");
                } else {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("gcoords")) {
            if (sender.isOp()) {
                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        Location targetLocation = target.getLocation();
                        sender.sendMessage(ChatColor.GREEN + "Coordinates of " + target.getName() + ":");
                        sender.sendMessage(ChatColor.GREEN + "X: " + coordinateFormat.format(targetLocation.getX()));
                        sender.sendMessage(ChatColor.GREEN + "Y: " + coordinateFormat.format(targetLocation.getY()));
                        sender.sendMessage(ChatColor.GREEN + "Z: " + coordinateFormat.format(targetLocation.getZ()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /gcoords <player>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("scoords")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length >= 1) {
                    StringBuilder message = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        message.append(args[i]).append(" ");
                    }
                    String targetName = args[0];
                    Player target = Bukkit.getPlayer(targetName);
                    if (target != null) {
                        Location playerLocation = player.getLocation();
                        target.sendMessage(ChatColor.GREEN + player.getName() + " shared their coordinates with you:");
                        target.sendMessage(ChatColor.GREEN + "X: " + coordinateFormat.format(playerLocation.getX()));
                        target.sendMessage(ChatColor.GREEN + "Y: " + coordinateFormat.format(playerLocation.getY()));
                        target.sendMessage(ChatColor.GREEN + "Z: " + coordinateFormat.format(playerLocation.getZ()));
                        player.sendMessage(ChatColor.GREEN + "You shared your coordinates with " + target.getName() + ".");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /scoords <player>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            }
            return true;
        }


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command! This command will not work in the terminal. :p");
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("c")) {
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(ChatColor.GREEN + "You are now in Creative mode!");
        } else if (command.getName().equalsIgnoreCase("s")) {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(ChatColor.GREEN + "You are now in Survival mode!");
        } else if (command.getName().equalsIgnoreCase("a")) {
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(ChatColor.GREEN + "You are now in Adventure mode!");
        } else if (command.getName().equalsIgnoreCase("sp")) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.GREEN + "You are now in Spectator mode!");
        }

        return true;
    }

    private void mutePlayer(String playerName) {
        mutedPlayers.add(playerName.toLowerCase());
    }

    private void unmutePlayer(String playerName) {
        mutedPlayers.remove(playerName.toLowerCase());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (mutedPlayers.contains(player.getName().toLowerCase())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are currently muted and cannot send messages.");
        }
    }

    private void broadcastMaintenanceMessage() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(ChatColor.GOLD + "Server is going down for maintenance. You may get kicked.");
            }
        });
    }

    private void playDingSound() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            }
        });
    }




    private void kickNonOpPlayers() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.isOp()) {
                maintenanceKickedPlayers.add(onlinePlayer.getName());
                onlinePlayer.kickPlayer(ChatColor.GOLD + "You have been kicked from the server for maintenance.");
            }
        }

    }



    private void displayServerInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "----- Server Information -----");
        sender.sendMessage(ChatColor.GREEN + "Server Version: " + Bukkit.getVersion());
        sender.sendMessage(ChatColor.GREEN + "Online Players: " + Bukkit.getOnlinePlayers().size());
        sender.sendMessage(ChatColor.GREEN + "Max Players: " + Bukkit.getMaxPlayers());
        sender.sendMessage(ChatColor.GREEN + "Server Port: " + Bukkit.getPort());
        sender.sendMessage(ChatColor.GREEN + "Max Render Distance: " + Bukkit.getViewDistance());
        sender.sendMessage(ChatColor.GREEN + "MOTD: " + Bukkit.getMotd());
        sender.sendMessage(ChatColor.YELLOW + "---- Performance Metrics ----");
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        sender.sendMessage(ChatColor.YELLOW + "Max Memory: " + maxMemory + " MB");
        sender.sendMessage(ChatColor.YELLOW + "Total Memory: " + totalMemory + " MB");
        sender.sendMessage(ChatColor.YELLOW + "Free Memory: " + freeMemory + " MB");
        sender.sendMessage(ChatColor.BLUE + "----- Server Uptime -----");
        long uptime = System.currentTimeMillis() - serverStartTime;
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime) % 60;

        sender.sendMessage(ChatColor.GREEN + "Server Uptime: " + days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds");


        sender.sendMessage(ChatColor.GREEN + "Server Uptime: " + days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
    }


    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (maintenanceMode && !Bukkit.getOfflinePlayer(event.getName()).isOp()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.GOLD + "The server is currently in maintenance mode. Try again later.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (maintenanceMode && !player.isOp()) {
            event.setJoinMessage(null);
            Bukkit.getScheduler().runTask(this, () -> player.kickPlayer(ChatColor.GOLD + "The server is currently in maintenance mode. Try again later."));
        }

    }




    private boolean backupWorld() {
        World world = Bukkit.getWorlds().get(0);
        File worldFolder = world.getWorldFolder().getAbsoluteFile().toPath().normalize().toFile();

        File backupFolder = new File(getDataFolder(), BACKUP_FOLDER_NAME);

        if (!backupFolder.exists()) {
            if (!backupFolder.mkdirs()) {
                getLogger().severe("Failed to create backup folder!");
                return false;
            }
        }

        String timestamp = String.valueOf(System.currentTimeMillis());

        File backupSubfolder = new File(backupFolder, timestamp);

        try {
            copyFolder(worldFolder.toPath(), backupSubfolder.toPath());
            getLogger().info("World backup created at: " + backupSubfolder.getAbsolutePath());
            return true;
        } catch (IOException e) {
            getLogger().severe("Failed to create world backup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void copyFolder(Path source, Path destination) throws IOException {
        Files.walk(source)
                .forEach(sourcePath -> {
                    Path destinationPath = destination.resolve(source.relativize(sourcePath));
                    try {
                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
