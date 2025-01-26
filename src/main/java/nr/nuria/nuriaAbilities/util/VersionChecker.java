package nr.nuria.nuriaAbilities.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {

    private final String versionAPIUrl = "https://raw.githubusercontent.com/devnuria/NuriaAPI/refs/heads/main/version.json";
    @Getter
    private final String currentVersion;
    private final JavaPlugin plugin;
    @Getter
    private String newVersion;

    public VersionChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.newVersion = null;
    }

    public void checkForUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(versionAPIUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(jsonResponse.toString());

                if (json.has("NuriaAbilities")) {
                    JSONObject nuriaAbilities = json.getJSONObject("NuriaAbilities");

                    if (nuriaAbilities.has("version")) {
                        newVersion = nuriaAbilities.getString("version");

                        if (versionCompare(currentVersion, newVersion) < 0) {
                            String message = "You are using an outdated version of NuriaAbilities! Please update to version " + newVersion + ".";
                            plugin.getLogger().warning(message);
                        }
                    } else {
                        plugin.getLogger().warning("The 'version' field is missing in the 'NuriaAbilities' object.");
                    }
                } else {
                    plugin.getLogger().warning("The 'NuriaAbilities' object is missing in the version.json file.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Method to compare versions
    private int versionCompare(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int v1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int v2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (v1 != v2) {
                return Integer.compare(v1, v2);
            }
        }
        return 0;
    }

    public boolean isUpdateAvailable() {
        return versionCompare(currentVersion, newVersion) < 0;
    }
}
