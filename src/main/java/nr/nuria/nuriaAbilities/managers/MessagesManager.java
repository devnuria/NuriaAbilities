package nr.nuria.nuriaAbilities.managers;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class MessagesManager {

    private final JavaPlugin plugin;
    @Getter
    private FileConfiguration messages;
    private File messagesFile;

    public MessagesManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createMessagesFile();
    }

    private void createMessagesFile() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadMessagesFile() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage(String key) {
        String message = messages.getString(key);

        if (message == null || message.trim().isEmpty()) {
            return null;
        }

        return message;
    }
}
