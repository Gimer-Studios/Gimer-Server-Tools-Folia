package me.liam.operator_tools;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final String versionInfoUrl;

    public UpdateChecker(JavaPlugin plugin, String versionInfoUrl) {
        this.plugin = plugin;
        this.versionInfoUrl = versionInfoUrl;
    }

    public void checkForUpdates() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(versionInfoUrl).openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("version=")) {
                    String availableVersion = line.substring("version=".length()).trim();
                    String currentVersion = plugin.getDescription().getVersion();

                    if (isNewVersionAvailable(currentVersion, availableVersion)) {
                        plugin.getLogger().info("A new version (" + availableVersion + ") is available! Download it at: " + getDownloadUrl());
                    } else {
                        plugin.getLogger().info("Your plugin is up to date!");
                    }

                    break;
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
        }
    }

    private boolean isNewVersionAvailable(String currentVersion, String availableVersion) {
        String[] currentParts = currentVersion.split("\\.");
        String[] availableParts = availableVersion.split("\\.");

        for (int i = 0; i < Math.min(currentParts.length, availableParts.length); i++) {
            int currentPart = Integer.parseInt(currentParts[i]);
            int availablePart = Integer.parseInt(availableParts[i]);

            if (currentPart < availablePart) {
                return true;
            } else if (currentPart > availablePart) {
                return false;
            }
        }

        return availableParts.length > currentParts.length;
    }

    private String getDownloadUrl() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(versionInfoUrl).openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("download_url=")) {
                    return line.substring("download_url=".length()).trim();
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to retrieve download URL: " + e.getMessage());
        }
        return "N/A";
    }
}