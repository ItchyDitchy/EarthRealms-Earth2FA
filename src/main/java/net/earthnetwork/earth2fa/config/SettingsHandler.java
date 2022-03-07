package net.earthnetwork.earth2fa.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import net.earthnetwork.earth2fa.Earth2FAPlugin;

public class SettingsHandler {

	private Earth2FAPlugin plugin = Earth2FAPlugin.getPlugin();
	
	private List<PotionEffectType> potionEffects = new ArrayList<PotionEffectType>();
	private boolean showScoreboard = true;
	
	public void reload() {
		File file = new File(plugin.getDataFolder(), "config.yml");
		if (!file.exists()) {
			plugin.saveResource("config.yml", false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String potionEffectKey : config.getStringList("potion_effects")) {
			for (PotionEffectType potionEffectType : PotionEffectType.values()) {
				if (potionEffectType.getName().equalsIgnoreCase(potionEffectKey)) {
					getPotionEffects().add(potionEffectType);
					break;
				}
			}
		}
		showScoreboard = config.getBoolean("scoreboard");
	}

	public boolean isShowScoreboard() {
		return showScoreboard;
	}

	public List<PotionEffectType> getPotionEffects() {
		return potionEffects;
	}
}
