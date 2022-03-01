package net.earthnetwork.id2fa.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private List<UUID> authenticatedUsers = new ArrayList<UUID>();

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

	public boolean playerInputCode(UUID uuid, int code) {
		String secretkey = secretKeys.get(uuid);

		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		boolean codeisvalid = gAuth.authorize(secretkey, code);


		if (codeisvalid) {
			authenticatePlayer(uuid);
			return codeisvalid;
		}

		return codeisvalid;
	}
	
	public boolean playerInputCode(Player player, int code) {
		return playerInputCode(player, code);
	}
	
	public boolean playerInputCode(OfflinePlayer offlinePlayer, int code) {
		return playerInputCode(offlinePlayer, code);
	}

	public boolean hasCode(UUID uuid) {
		return secretKeys.containsKey(uuid);
	}
	
	public boolean hasCode(Player player) {
		return hasCode(player.getUniqueId());
	}
	
	public boolean hasCode(OfflinePlayer offlinePlayer) {
		return hasCode(offlinePlayer.getUniqueId());
	}
	
	public void unauthenticatePlayer(UUID uuid) {
		authenticatedUsers.remove(uuid);
	}

	public void unauthenticatePlayer(Player player) {
		unauthenticatePlayer(player.getUniqueId());
	}

	public void unauthenticatePlayer(OfflinePlayer offlinePlayer) {
		unauthenticatePlayer(offlinePlayer.getUniqueId());
	}

	public void authenticatePlayer(UUID uuid) {
		authenticatedUsers.add(uuid);
	}

	public void authenticatePlayer(Player player) {
		authenticatePlayer(player.getUniqueId());
	}

	public void authenticatePlayer(OfflinePlayer offlinePlayer) {
		authenticatePlayer(offlinePlayer.getUniqueId());
	}

	public boolean isPlayerAuthenticated(UUID uuid) {
		return !secretKeys.containsKey(uuid) || authenticatedUsers.contains(uuid);
	}

	public boolean isPlayerAuthenticated(Player player) {
		return isPlayerAuthenticated(player.getUniqueId());
	}

	public boolean isPlayerAuthenticated(OfflinePlayer offlinePlayer) {
		return isPlayerAuthenticated(offlinePlayer.getUniqueId());
	}
}
