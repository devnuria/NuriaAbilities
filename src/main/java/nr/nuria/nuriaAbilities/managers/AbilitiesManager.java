package nr.nuria.nuriaAbilities.managers;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AbilitiesManager {
    @Getter

    private final JavaPlugin plugin;
    @Getter
    private FileConfiguration abilitiesConfig;
    private File abilitiesFile;

    public AbilitiesManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createAbilitiesFile();
    }

    private void createAbilitiesFile() {
        abilitiesFile = new File(plugin.getDataFolder(), "abilities.yml");
        if (!abilitiesFile.exists()) {
            abilitiesFile.getParentFile().mkdirs();
            plugin.saveResource("abilities.yml", false);
        }

        abilitiesConfig = YamlConfiguration.loadConfiguration(abilitiesFile);
    }
    public void reloadAbilitiesFile() {
        abilitiesConfig = YamlConfiguration.loadConfiguration(abilitiesFile);
    }

    public void saveConfig() {
        try {
            abilitiesConfig.save(abilitiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getTrapperName() {
        return abilitiesConfig.getString("trapper.name", "&a&lTRAPPER");
    }

    public Material getTrapperMaterial() {
        return Material.valueOf(abilitiesConfig.getString("trapper.material", "IRON_BARS"));
    }

    public List<String> getTrapperLore() {
        return abilitiesConfig.getStringList("trapper.lore");
    }

    public boolean isTrapperGlowing() {
        return abilitiesConfig.getBoolean("trapper.glowing", true);
    }

    public Material getTrapperSphereMaterial() {
        return Material.valueOf(abilitiesConfig.getString("trapper.sphere-material", "GLASS"));
    }

    public int getTrapperSphereDuration() {
        return abilitiesConfig.getInt("trapper.sphere-duration", 5) * 20; // convert to ticks
    }

    public int getTrapperSphereRadius() {
        return abilitiesConfig.getInt("trapper.sphere-radius", 5);
    }

    public int getTrapperReduceAmount() {
        return abilitiesConfig.getInt("trapper.reduce-amount", 1);
    }

    public int getTrapperCooldown() {
        return abilitiesConfig.getInt("trapper.cooldown", -1);
    }

    public String getTrapperStartSoundName() {
        return abilitiesConfig.getString("trapper.effects.start.sound.name", "BLOCK_NOTE_BLOCK_PLING");
    }

    public float getTrapperStartSoundPitch() {
        return (float) abilitiesConfig.getDouble("trapper.effects.start.sound.pitch", 1.0);
    }

    public float getTrapperStartSoundVolume() {
        return (float) abilitiesConfig.getDouble("trapper.effects.start.sound.volume", 1.0);
    }

    public String getTrapperStartParticle() {
        return abilitiesConfig.getString("trapper.effects.start.particle", "SPELL_WITCH");
    }

    public String getTrapperEndSoundName() {
        return abilitiesConfig.getString("trapper.effects.end.sound.name", "ENTITY_GENERIC_EXPLODE");
    }

    public float getTrapperEndSoundPitch() {
        return (float) abilitiesConfig.getDouble("trapper.effects.end.sound.pitch", 1.0);
    }

    public float getTrapperEndSoundVolume() {
        return (float) abilitiesConfig.getDouble("trapper.effects.end.sound.volume", 1.0);
    }

    public String getTrapperEndParticle() {
        return abilitiesConfig.getString("trapper.effects.end.particle", "EXPLOSION_NORMAL");
    }
    public String getTrapperTexture() {
        return abilitiesConfig.getString("trapper.texture", "");
    }

    public List<Material> getTrapperReplaceableMaterials() {
        List<String> materialNames = abilitiesConfig.getStringList("trapper.replaceable-materials");
        List<Material> materials = new ArrayList<>();
        for (String name : materialNames) {
            try {
                materials.add(Material.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material in replaceable-materials: " + name);
            }
        }
        return materials;
    }

    public String getRevolverName() {
        return abilitiesConfig.getString("revolver.name", "&8&lREVOLVER");
    }

    public Material getRevolverMaterial() {
        return Material.valueOf(abilitiesConfig.getString("revolver.material", "BLAZE_ROD"));
    }

    public List<String> getRevolverLore() {
        return abilitiesConfig.getStringList("revolver.lore");
    }

    public boolean isRevolverGlowing() {
        return abilitiesConfig.getBoolean("revolver.glowing", true);
    }

    public int getRevolverHits() {
        return abilitiesConfig.getInt("revolver.hits", 1);
    }

    public int getRevolverReduceAmount() {
        return abilitiesConfig.getInt("revolver.reduce-amount", 1);
    }

    public int getRevolverCooldown() {
        return abilitiesConfig.getInt("revolver.cooldown", -1);
    }

    public String getRevolverMode() {
        return abilitiesConfig.getString("revolver.mode", "hotbar+offhand");
    }

    public String getRevolverStartSoundName() {
        return abilitiesConfig.getString("revolver.effects.start.sound.name", "BLOCK_NOTE_BLOCK_PLING");
    }

    public float getRevolverStartSoundPitch() {
        return (float) abilitiesConfig.getDouble("revolver.effects.start.sound.pitch", 1.0);
    }

    public float getRevolverStartSoundVolume() {
        return (float) abilitiesConfig.getDouble("revolver.effects.start.sound.volume", 1.0);
    }

    public String getRevolverStartParticle() {
        return abilitiesConfig.getString("revolver.effects.start.particle", "SPELL_WITCH");
    }

    public String getRevolverEndSoundName() {
        return abilitiesConfig.getString("revolver.effects.end.sound.name", "ENTITY_GENERIC_EXPLODE");
    }

    public float getRevolverEndSoundPitch() {
        return (float) abilitiesConfig.getDouble("revolver.effects.end.sound.pitch", 1.0);
    }

    public float getRevolverEndSoundVolume() {
        return (float) abilitiesConfig.getDouble("revolver.effects.end.sound.volume", 1.0);
    }

    public String getRevolverEndParticle() {
        return abilitiesConfig.getString("revolver.effects.end.particle", "EXPLOSION_NORMAL");
    }
    public String getRevolverTexture() {
        return abilitiesConfig.getString("revolver.texture", "");
    }
    public String getWebtrapName() {
        return abilitiesConfig.getString("webtrap.name", "&c&lWEBTRAP");
    }

    public Material getWebtrapMaterial() {
        return Material.valueOf(abilitiesConfig.getString("webtrap.material", "FIREBALL"));
    }

    public List<String> getWebtrapLore() {
        return abilitiesConfig.getStringList("webtrap.lore");
    }

    public boolean isWebtrapGlowing() {
        return abilitiesConfig.getBoolean("webtrap.glowing", true);
    }

    public int getWebtrapCooldown() {
        return abilitiesConfig.getInt("webtrap.cooldown", -1);
    }

    public int getWebtrapReduceAmount() {
        return abilitiesConfig.getInt("webtrap.reduce-amount", 1);
    }

    public int getWebtrapHits() {
        return abilitiesConfig.getInt("webtrap.hits", 1);
    }

    public int getWebtrapDuration() {
        return abilitiesConfig.getInt("webtrap.duration", -1);
    }

    public String getWebtrapStartSoundName() {
        return abilitiesConfig.getString("webtrap.effects.start.sound.name", "BLOCK_NOTE_BLOCK_PLING");
    }

    public float getWebtrapStartSoundPitch() {
        return (float) abilitiesConfig.getDouble("webtrap.effects.start.sound.pitch", 1.0);
    }

    public float getWebtrapStartSoundVolume() {
        return (float) abilitiesConfig.getDouble("webtrap.effects.start.sound.volume", 1.0);
    }

    public String getWebtrapStartParticle() {
        return abilitiesConfig.getString("webtrap.effects.start.particle", "SPELL_WITCH");
    }

    public String getWebtrapEndSoundName() {
        return abilitiesConfig.getString("webtrap.effects.end.sound.name", "ENTITY_GENERIC_EXPLODE");
    }

    public float getWebtrapEndSoundPitch() {
        return (float) abilitiesConfig.getDouble("webtrap.effects.end.sound.pitch", 1.0);
    }

    public float getWebtrapEndSoundVolume() {
        return (float) abilitiesConfig.getDouble("webtrap.effects.end.sound.volume", 1.0);
    }

    public String getWebtrapEndParticle() {
        return abilitiesConfig.getString("webtrap.effects.end.particle", "EXPLOSION_NORMAL");
    }
    public int getWebtrapHeight() {
        return abilitiesConfig.getInt("webtrap.height", 1); // Default to 3 if not specified
    }

    public int getWebtrapWidth() {
        return abilitiesConfig.getInt("webtrap.width", 1); // Default to 3 if not specified
    }
    public String getWebtrapTexture() {
        return abilitiesConfig.getString("webtrap.texture", "");
    }
    public List<Material> getWebtrapReplaceableMaterials() {
        List<String> materialNames = abilitiesConfig.getStringList("webtrap.replaceable-materials");
        List<Material> materials = new ArrayList<>();
        for (String name : materialNames) {
            try {
                materials.add(Material.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material in replaceable-materials: " + name);
            }
        }
        return materials;
    }
    public String getWebdelName() {
        return abilitiesConfig.getString("webdel.name", "&b&lWEBDEL");
    }

    public Material getWebdelMaterial() {
        return Material.valueOf(abilitiesConfig.getString("webdel.material", "BLAZE_ROD"));
    }

    public List<String> getWebdelLore() {
        return abilitiesConfig.getStringList("webdel.lore");
    }

    public boolean isWebdelGlowing() {
        return abilitiesConfig.getBoolean("webdel.glowing", true);
    }

    public int getWebdelCooldown() {
        return abilitiesConfig.getInt("webdel.cooldown", -1);
    }

    public int getWebdelReduceAmount() {
        return abilitiesConfig.getInt("webdel.reduce-amount", 1);
    }

    public int getWebdelWidth() {
        return abilitiesConfig.getInt("webdel.width", 3);
    }

    public int getWebdelHeight() {
        return abilitiesConfig.getInt("webdel.height", 3);
    }

    public String getWebdelStartSoundName() {
        return abilitiesConfig.getString("webdel.effects.start.sound.name", "BLOCK_NOTE_BLOCK_PLING");
    }

    public float getWebdelStartSoundPitch() {
        return (float) abilitiesConfig.getDouble("webdel.effects.start.sound.pitch", 1.0);
    }

    public float getWebdelStartSoundVolume() {
        return (float) abilitiesConfig.getDouble("webdel.effects.start.sound.volume", 1.0);
    }

    public String getWebdelStartParticle() {
        return abilitiesConfig.getString("webdel.effects.start.particle", "SPELL_WITCH");
    }
    public String getWebdelTexture() {
        return abilitiesConfig.getString("webdel.texture", "");
    }
    public String getAntiElytraName() {
        return abilitiesConfig.getString("antielytra.name", "#cda3ff&lANTIELYTRA &8◌ &7Clic der");
    }

    public Material getAntiElytraMaterial() {
        return Material.valueOf(abilitiesConfig.getString("antielytra.material", "HEART_OF_THE_SEA"));
    }

    public List<String> getAntiElytraLore() {
        return abilitiesConfig.getStringList("antielytra.lore");
    }

    public boolean isAntiElytraGlowing() {
        return abilitiesConfig.getBoolean("antielytra.glowing", true);
    }

    public int getAntiElytraReduceAmount() {
        return abilitiesConfig.getInt("antielytra.reduce-amount", 1);
    }

    public int getAntiElytraCooldown() {
        return abilitiesConfig.getInt("antielytra.cooldown", -1);
    }

    public int getAntiElytraDuration() {
        return abilitiesConfig.getInt("antielytra.duration", 10); // in seconds
    }

    public String getAntiElytraStartSoundName() {
        return abilitiesConfig.getString("antielytra.effects.start.sound.name", "BLOCK_NOTE_BLOCK_PLING");
    }

    public float getAntiElytraStartSoundPitch() {
        return (float) abilitiesConfig.getDouble("antielytra.effects.start.sound.pitch", 1.0);
    }

    public float getAntiElytraStartSoundVolume() {
        return (float) abilitiesConfig.getDouble("antielytra.effects.start.sound.volume", 1.0);
    }

    public String getAntiElytraStartParticle() {
        return abilitiesConfig.getString("antielytra.effects.start.particle", "SPELL_WITCH");
    }

    public String getAntiElytraEndSoundName() {
        return abilitiesConfig.getString("antielytra.effects.end.sound.name", "ENTITY_GENERIC_EXPLODE");
    }

    public float getAntiElytraEndSoundPitch() {
        return (float) abilitiesConfig.getDouble("antielytra.effects.end.sound.pitch", 1.0);
    }

    public float getAntiElytraEndSoundVolume() {
        return (float) abilitiesConfig.getDouble("antielytra.effects.end.sound.volume", 1.0);
    }

    public String getAntiElytraEndParticle() {
        return abilitiesConfig.getString("antielytra.effects.end.particle", "EXPLOSION_NORMAL");
    }
    public String getAntiElytraTexture() {
        return abilitiesConfig.getString("antielytra.texture", "");
    }
    public String getTimeMachineName() {
        return abilitiesConfig.getString("timemachine.name", "&b&lTIME MACHINE");
    }

    public Material getTimeMachineMaterial() {
        return Material.valueOf(abilitiesConfig.getString("timemachine.material", "CLOCK"));
    }

    public String getTimeMachineTexture() {
        return abilitiesConfig.getString("timemachine.texture", "");
    }

    public List<String> getTimeMachineLore() {
        return abilitiesConfig.getStringList("timemachine.lore");
    }

    public boolean isTimeMachineGlowing() {
        return abilitiesConfig.getBoolean("timemachine.glowing", true);
    }

    public int getTimeMachineDuration() {
        return abilitiesConfig.getInt("timemachine.duration", 5) * 20; // Convertido a ticks
    }
    public int getTimeMachineReduceAmount() {
        return abilitiesConfig.getInt("timemachine.reduce-amount", 1);
    }
    public int getTimeMachineCooldown() {
        return abilitiesConfig.getInt("timemachine.cooldown", -1); // Cooldown en segundos
    }

    public String getTimeMachineStartSoundName() {
        return abilitiesConfig.getString("timemachine.effects.start.sound.name", "BLOCK_NOTE_BLOCK_PLING");
    }

    public float getTimeMachineStartSoundPitch() {
        return (float) abilitiesConfig.getDouble("timemachine.effects.start.sound.pitch", 1.0);
    }

    public float getTimeMachineStartSoundVolume() {
        return (float) abilitiesConfig.getDouble("timemachine.effects.start.sound.volume", 1.0);
    }

    public String getTimeMachineStartParticle() {
        return abilitiesConfig.getString("timemachine.effects.start.particle", "SPELL_WITCH");
    }

    public String getTimeMachineEndSoundName() {
        return abilitiesConfig.getString("timemachine.effects.end.sound.name", "ENTITY_GENERIC_EXPLODE");
    }

    public float getTimeMachineEndSoundPitch() {
        return (float) abilitiesConfig.getDouble("timemachine.effects.end.sound.pitch", 1.0);
    }

    public float getTimeMachineEndSoundVolume() {
        return (float) abilitiesConfig.getDouble("timemachine.effects.end.sound.volume", 1.0);
    }

    public String getTimeMachineEndParticle() {
        return abilitiesConfig.getString("timemachine.effects.end.particle", "EXPLOSION_NORMAL");
    }

    public String getAntiBuildName() {
        return abilitiesConfig.getString("antibuild.name", "&b&lANTIBUILD");
    }

    public Material getAntiBuildMaterial() {
        return Material.valueOf(abilitiesConfig.getString("antibuild.material", "DIAMOND_SWORD"));
    }

    public List<String> getAntiBuildLore() {
        return abilitiesConfig.getStringList("antibuild.lore");
    }

    public boolean isAntiBuildGlowing() {
        return abilitiesConfig.getBoolean("antibuild.glowing", true);
    }

    public int getAntiBuildDuration() {
        return abilitiesConfig.getInt("antibuild.duration", 10); // in seconds
    }

    public int getAntiBuildHits() {
        return abilitiesConfig.getInt("antibuild.hits", 3);
    }

    public int getAntiBuildReduceAmount() {
        return abilitiesConfig.getInt("antibuild.reduce-amount", 1);
    }

    public int getAntiBuildCooldown() {
        return abilitiesConfig.getInt("antibuild.cooldown", -1);
    }

    public String getAntiBuildStartSoundName() {
        return abilitiesConfig.getString("antibuild.effects.start.sound.name", "BLOCK_NOTE_BLOCK_PLING");
    }

    public float getAntiBuildStartSoundPitch() {
        return (float) abilitiesConfig.getDouble("antibuild.effects.start.sound.pitch", 1.0);
    }

    public float getAntiBuildStartSoundVolume() {
        return (float) abilitiesConfig.getDouble("antibuild.effects.start.sound.volume", 1.0);
    }

    public String getAntiBuildStartParticle() {
        return abilitiesConfig.getString("antibuild.effects.start.particle", "SPELL_WITCH");
    }

    public String getAntiBuildEndSoundName() {
        return abilitiesConfig.getString("antibuild.effects.end.sound.name", "ENTITY_GENERIC_EXPLODE");
    }

    public float getAntiBuildEndSoundPitch() {
        return (float) abilitiesConfig.getDouble("antibuild.effects.end.sound.pitch", 1.0);
    }

    public float getAntiBuildEndSoundVolume() {
        return (float) abilitiesConfig.getDouble("antibuild.effects.end.sound.volume", 1.0);
    }

    public String getAntiBuildEndParticle() {
        return abilitiesConfig.getString("antibuild.effects.end.particle", "EXPLOSION_NORMAL");
    }

    public List<Material> getAntiBuildRestrictedMaterials() {
        List<String> materialNames = abilitiesConfig.getStringList("antibuild.restricted-materials");
        List<Material> materials = new ArrayList<>();
        for (String materialName : materialNames) {
            materials.add(Material.valueOf(materialName));
        }
        return materials;
    }
    public String getAntiBuildTexture() {
        return abilitiesConfig.getString("antibuild.texture", "");
    }

}