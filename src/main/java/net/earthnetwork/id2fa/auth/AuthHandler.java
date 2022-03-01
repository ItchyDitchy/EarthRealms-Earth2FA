package net.earthnetwork.id2fa.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import net.earthnetwork.id2fa.ID2FAPlugin;

public class AuthHandler {

	private Map<UUID, String> secretKeys = new HashMap<UUID, String>();
	private ID2FAPlugin plugin;
	
	public AuthHandler(ID2FAPlugin plugin) {
		this.plugin = plugin;
		plugin.getConfig().options().copyDefaults(true);
	}

	public void loadData() {
		File file = new File(plugin.getDataFolder(), "data.yml");
		if (!file.exists()) {
			plugin.saveResource("data.yml", false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String key : config.getKeys(false)) {
			secretKeys.put(UUID.fromString(key), config.getString(key));
		}
	}
	
	public void saveData() {
		File file = new File(plugin.getDataFolder(), "data.yml");
		if (!file.exists()) {
			plugin.saveResource("data.yml", false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (UUID uuid : secretKeys.keySet()) {
			config.set(uuid.toString(), secretKeys.get(uuid));
		}
		try {
			config.save(file);
		} catch (IOException exception) {
			 exception.printStackTrace();
		}
	}
	
	private boolean playerInputCode(Player player, int code) {
		String secretkey = this.getConfig().getString("authcodes." + player.getUniqueId());

		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		boolean codeisvalid = gAuth.authorize(secretkey, code);


		if (codeisvalid) {
			authlocked.remove(player.getUniqueId());
			return codeisvalid;
		}

		return codeisvalid;
	}
	
	public void authenticatePlayer(UUID uuid) {
		
	}
	
	public void authenticatePlayer(Player player) {
		authenticatePlayer(player.getUniqueId());
	}
	
	public void authenticatePlayer(OfflinePlayer offlinePlayer) {
		authenticatePlayer(offlinePlayer.getUniqueId());
	}
	
	public ArrayList<UUID> getAuthlocked() {
		return authlocked;
	}

	public void setAuthlocked(ArrayList<UUID> authlocked) {
		this.authlocked = authlocked;
	}
}
