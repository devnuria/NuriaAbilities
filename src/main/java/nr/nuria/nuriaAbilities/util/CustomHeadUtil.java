package nr.nuria.nuriaAbilities.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class CustomHeadUtil {


    public static ItemStack createCustomHead(String textureUrl) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());

        try {
            profile.getTextures().setSkin(new URL(textureUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        headMeta.setOwnerProfile(profile);

        head.setItemMeta(headMeta);

        return head;
    }

    public static void giveCustomHead(Player player, String textureUrl) {
        ItemStack customHead = createCustomHead(textureUrl);
        player.getInventory().addItem(customHead);
    }
}
