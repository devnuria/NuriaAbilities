package nr.nuria.nuriaAbilities.abilities.manager;

import nr.nuria.nuriaAbilities.NuriaAbilities;
import nr.nuria.nuriaAbilities.managers.AbilitiesManager;
import nr.nuria.nuriaAbilities.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class AbilitiesItemManager {


    private static final NamespacedKey ANTIBUILD_KEY = new NamespacedKey(NuriaAbilities.getInstance(), "antibuild");
    private static final NamespacedKey TIMEMACHINE_KEY = new NamespacedKey(NuriaAbilities.getInstance(), "timemachine");
    private static final NamespacedKey ANTIELYTRA_KEY = new NamespacedKey(NuriaAbilities.getInstance(), "antielytra");
    private static final NamespacedKey TRAPPER_KEY = new NamespacedKey(NuriaAbilities.getInstance(), "trapper");
    private static final NamespacedKey REVOLVER_KEY = new NamespacedKey(NuriaAbilities.getInstance(), "revolver");
    private static final NamespacedKey WEBTRAP_KEY = new NamespacedKey(NuriaAbilities.getInstance(), "webtrap");
    private static final NamespacedKey WEBDEL_KEY = new NamespacedKey(NuriaAbilities.getInstance(), "webdel");
    private static final AbilitiesManager abilitiesManager = NuriaAbilities.getInstance().getAbilitiesManager();

    private static void setHeadTexture(ItemStack item, String textureUrl) {
        if (item.getType() == Material.PLAYER_HEAD && textureUrl != null && !textureUrl.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "CustomHead");
                try {
                    profile.getTextures().setSkin(new URL(textureUrl));
                    skullMeta.setOwnerProfile(profile);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                item.setItemMeta(skullMeta);
            }
        }
    }

    public static ItemStack createAntiElytraItem() {
        ItemStack item = new ItemStack(abilitiesManager.getAntiElytraMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.format(abilitiesManager.getAntiElytraName()));
            meta.setLore(ColorUtil.formatList(abilitiesManager.getAntiElytraLore()));
            if (abilitiesManager.isAntiElytraGlowing()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.getPersistentDataContainer().set(ANTIELYTRA_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            setHeadTexture(item, abilitiesManager.getAntiElytraTexture());
        }
        return item;
    }

    public static boolean isAntiElytraItem(ItemStack item) {
        if (item == null || item.getType() != abilitiesManager.getAntiElytraMaterial()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(ANTIELYTRA_KEY, PersistentDataType.BYTE);
    }

    public static ItemStack createTrapperItem() {
        ItemStack item = new ItemStack(abilitiesManager.getTrapperMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.format(abilitiesManager.getTrapperName()));
            meta.setLore(ColorUtil.formatList(abilitiesManager.getTrapperLore()));
            if (abilitiesManager.isTrapperGlowing()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.getPersistentDataContainer().set(TRAPPER_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            setHeadTexture(item, abilitiesManager.getTrapperTexture());
        }
        return item;
    }

    public static boolean isTrapperItem(ItemStack item) {
        if (item == null || item.getType() != abilitiesManager.getTrapperMaterial()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(TRAPPER_KEY, PersistentDataType.BYTE);
    }

    public static ItemStack createRevolverItem() {
        ItemStack item = new ItemStack(abilitiesManager.getRevolverMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.format(abilitiesManager.getRevolverName()));
            meta.setLore(ColorUtil.formatList(abilitiesManager.getRevolverLore()));
            if (abilitiesManager.isRevolverGlowing()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.getPersistentDataContainer().set(REVOLVER_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            setHeadTexture(item, abilitiesManager.getRevolverTexture());
        }
        return item;
    }

    public static boolean isRevolverItem(ItemStack item) {
        if (item == null || item.getType() != abilitiesManager.getRevolverMaterial()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(REVOLVER_KEY, PersistentDataType.BYTE);
    }

    public static ItemStack createWebtrapItem() {
        ItemStack item = new ItemStack(abilitiesManager.getWebtrapMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.format(abilitiesManager.getWebtrapName()));
            meta.setLore(ColorUtil.formatList(abilitiesManager.getWebtrapLore()));
            if (abilitiesManager.isWebtrapGlowing()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.getPersistentDataContainer().set(WEBTRAP_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            setHeadTexture(item, abilitiesManager.getWebtrapTexture());
        }
        return item;
    }

    public static boolean isWebtrapItem(ItemStack item) {
        if (item == null || item.getType() != abilitiesManager.getWebtrapMaterial()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(WEBTRAP_KEY, PersistentDataType.BYTE);
    }

    public static ItemStack createWebdelItem() {
        ItemStack item = new ItemStack(abilitiesManager.getWebdelMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorUtil.format(abilitiesManager.getWebdelName()));
            meta.setLore(ColorUtil.formatList(abilitiesManager.getWebdelLore()));
            if (abilitiesManager.isWebdelGlowing()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.getPersistentDataContainer().set(WEBDEL_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            setHeadTexture(item, abilitiesManager.getWebdelTexture());
        }

        return item;
    }

    public static boolean isWebdelItem(ItemStack item) {
        if (item == null || item.getType() != abilitiesManager.getWebdelMaterial()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(WEBDEL_KEY, PersistentDataType.BYTE);
    }

    public static ItemStack createTimeMachineItem() {
        ItemStack item = new ItemStack(abilitiesManager.getTimeMachineMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorUtil.format(abilitiesManager.getTimeMachineName()));
            meta.setLore(ColorUtil.formatList(abilitiesManager.getTimeMachineLore()));
            if (abilitiesManager.isTimeMachineGlowing()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            meta.getPersistentDataContainer().set(TIMEMACHINE_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            setHeadTexture(item, abilitiesManager.getTimeMachineTexture());
        }

        return item;
    }

    public static boolean isTimeMachineItem(ItemStack item) {
        if (item == null || item.getType() != abilitiesManager.getTimeMachineMaterial()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(TIMEMACHINE_KEY, PersistentDataType.BYTE);
    }

    public static ItemStack createAntiBuildItem() {
        ItemStack item = new ItemStack(abilitiesManager.getAntiBuildMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorUtil.format(abilitiesManager.getAntiBuildName()));
            meta.setLore(ColorUtil.formatList(abilitiesManager.getAntiBuildLore()));
            if (abilitiesManager.isAntiBuildGlowing()) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            meta.getPersistentDataContainer().set(ANTIBUILD_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            setHeadTexture(item, abilitiesManager.getAntiBuildTexture());
        }

        return item;
    }

    public static boolean isAntiBuildItem(ItemStack item) {
        if (item == null || item.getType() != abilitiesManager.getAntiBuildMaterial()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(ANTIBUILD_KEY, PersistentDataType.BYTE);
    }

}
