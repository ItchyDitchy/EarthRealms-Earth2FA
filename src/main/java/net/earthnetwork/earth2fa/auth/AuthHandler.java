package net.earthnetwork.earth2fa.auth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import net.earthnetwork.earth2fa.Earth2FAPlugin;
import net.earthnetwork.earth2fa.commands.Cmd2FA;
import net.earthnetwork.earth2fa.listeners.PlayerListener;

public class AuthHandler {

	private Map<UUID, String> secretKeys = new HashMap<UUID, String>();
	private Map<UUID, Long> authenticatedUsers = new HashMap<UUID, Long>();

	private Earth2FAPlugin plugin = Earth2FAPlugin.getPlugin();

	/**
	 * A constructor for the Authentication Handler.
	 * 
	 * @param plugin An instance of the main class.
	 */
	public AuthHandler() {
		new Cmd2FA(this);
		new PlayerListener(this);
		plugin.getConfig().options().copyDefaults(true);
	}

	/**
	 * Loads the data from the <b>database.yml</b>
	 */
	public void loadData() {
		File file = new File(plugin.getDataFolder(), "database.yml");
		if (!file.exists()) {
			plugin.saveResource("database.yml", false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String key : config.getKeys(false)) {
			secretKeys.put(UUID.fromString(key), config.getString(key));
		}
	}

	/**
	 * Saves the data for secret keys.
	 */
	public void saveData() {
		File file = new File(plugin.getDataFolder(), "database.yml");
		if (!file.exists()) {
			plugin.saveResource("database.yml", false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String key : config.getKeys(false)) {
			config.set(key, null);
		}
		for (UUID uuid : secretKeys.keySet()) {
			config.set(uuid.toString(), secretKeys.get(uuid));
		}
		try {
			config.save(file);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Checks if the code that corresponds to the uuid is correct.
	 * 
	 * @param uuid A uuid of a player.
	 * @param code The 6-digit code to be checked.
	 * @return True if the code is correct and false if not.
	 */
	public boolean playerInputCode(UUID uuid, int code) {
		String secretkey = secretKeys.get(uuid);

		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		boolean codeisvalid = gAuth.authorize(secretkey, code);

		return codeisvalid;
	}
	
	/**
	 * Checks if the code that corresponds to the uuid is correct.
	 * 
	 * @param player The player to get the uuid to be used from.
	 * @param code The 6-digit code to be checked.
	 * @return True if the code is correct and false if not.
	 */
	public boolean playerInputCode(Player player, int code) {
		return playerInputCode(player.getUniqueId(), code);
	}
	
	/**
	 * Checks if the code that corresponds to the uuid is correct.
	 * 
	 * @param offlinePlayer The offline player to get the uuid to be used from.
	 * @param code The 6-digit code to be checked.
	 * @return True if the code is correct and false if not.
	 */
	public boolean playerInputCode(OfflinePlayer offlinePlayer, int code) {
		return playerInputCode(offlinePlayer.getUniqueId(), code);
	}

	/**
	 * Registers the player.
	 * 
	 * @param uuid The uuid used to get the player.
	 * @param key The key to the user's authentication.
	 */
	public void registerPlayer(UUID uuid, String key) {
		secretKeys.put(uuid, key);
	}
	
	/**
	 * Registers the player.
	 * 
	 * @param player The player to register.
	 * @param key The key to the user's authentication.
	 */
	public void registerPlayer(Player player, String key) {
		registerPlayer(player.getUniqueId(), key);
	}
	
	/**
	 * Registers the player.
	 * 
	 * @param player The player to register.
	 * @param key The key to the user's authentication.
	 */
	public void registerPlayer(OfflinePlayer offlinePlayer, String key) {
		registerPlayer(offlinePlayer.getUniqueId(), key);
	}
	
	/**
	 * Unregisters the player and removes the current key to their uuid.
	 * 
	 * @param uuid The uuid used to get the player.
	 */
	public void unregisterPlayer(UUID uuid) {
		if (secretKeys.containsKey(uuid)) {
			secretKeys.remove(uuid);
		}
		if (authenticatedUsers.containsKey(uuid)) {
			authenticatedUsers.remove(uuid);
		}
	}

	/**
	 * Unregisters the player and removes the current key to their uuid.
	 * 
	 * @param player The player to get the uuid to be unregistered.
	 */
	public void unregisterPlayer(Player player) {
		unregisterPlayer(player.getUniqueId());
	}

	/**
	 * Unregisters the player and removes the current key to their uuid.
	 * 
	 * @param uuid The offline player to get the uuid to be unregistered.
	 */
	public void unregisterPlayer(OfflinePlayer offlinePlayer) {
		unregisterPlayer(offlinePlayer.getUniqueId());
	}
	
	/**
	 * Checks if a player is registered.
	 * 
	 * @param uuid The uuid of the player to check if is unregistered.
	 * @return True if the player is registered and false if not.
	 */
	public boolean hasCode(UUID uuid) {
		return secretKeys.containsKey(uuid);
	}
	
	/**
	 * Checks if a player is registered.
	 * 
	 * @param uuid The player to check if is unregistered.
	 * @return True if the player is registered and false if not.
	 */
	public boolean hasCode(Player player) {
		return hasCode(player.getUniqueId());
	}
	
	/**
	 * Checks if a player is registered.
	 * 
	 * @param uuid The offline player to check if is unregistered.
	 * @return True if the player is registered and false if not.
	 */
	public boolean hasCode(OfflinePlayer offlinePlayer) {
		return hasCode(offlinePlayer.getUniqueId());
	}
	
	/**
	 * Unauthenticates a player and forces them to type in the current code.
	 * 
	 * @param uuid The uuid of the player to unauthenticate.
	 */
	public void unauthenticatePlayer(UUID uuid) {
		authenticatedUsers.remove(uuid);
	}

	/**
	 * Unauthenticates a player and forces them to type in the current code.
	 * 
	 * @param player The player to unauthenticate.
	 */
	public void unauthenticatePlayer(Player player) {
		unauthenticatePlayer(player.getUniqueId());
	}

	/**
	 * Unauthenticates a player and forces them to type in the current code.
	 * 
	 * @param offlinePlayer The player to unauthenticate.
	 */
	public void unauthenticatePlayer(OfflinePlayer offlinePlayer) {
		unauthenticatePlayer(offlinePlayer.getUniqueId());
	}

	/**
	 * Authenticates a player and forces them to type in the current code.
	 * 
	 * @param uuid The uuid of the player to authenticate.
	 */
	public void authenticatePlayer(UUID uuid) {
		authenticatedUsers.put(uuid, System.currentTimeMillis() + (1000*60*30));
	}

	/**
	 * Authenticates a player and forces them to type in the current code.
	 * 
	 * @param player The player to authenticate.
	 */
	public void authenticatePlayer(Player player) {
		authenticatePlayer(player.getUniqueId());
	}

	/**
	 * Authenticates a player and forces them to type in the current code.
	 * 
	 * @param offlinePlayer The player to authenticate.
	 */
	public void authenticatePlayer(OfflinePlayer offlinePlayer) {
		authenticatePlayer(offlinePlayer.getUniqueId());
	}

	/**
	 * Checks if the player is authenticated.
	 * 
	 * @param uuid The uuid of the player to check for authentication.
	 * @return True if the player is authenticated and false if not.
	 */
	public boolean isPlayerAuthenticated(UUID uuid) {
		return !secretKeys.containsKey(uuid) || (authenticatedUsers.containsKey(uuid) && authenticatedUsers.get(uuid) >= System.currentTimeMillis());
	}

	/**
	 * Checks if the player is authenticated.
	 * 
	 * @param player The player to check for authentication.
	 * @return True if the player is authenticated and false if not.
	 */
	public boolean isPlayerAuthenticated(Player player) {
		return isPlayerAuthenticated(player.getUniqueId());
	}

	/**
	 * Checks if the player is authenticated.
	 * 
	 * @param offlinePlayer The player to check for authentication.
	 * @return True if the player is authenticated and false if not.
	 */
	public boolean isPlayerAuthenticated(OfflinePlayer offlinePlayer) {
		return isPlayerAuthenticated(offlinePlayer.getUniqueId());
	}
}
