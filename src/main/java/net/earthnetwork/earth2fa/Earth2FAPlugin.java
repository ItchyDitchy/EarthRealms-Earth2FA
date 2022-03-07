package net.earthnetwork.earth2fa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import net.earthnetwork.earth2fa.auth.AuthHandler;
import net.earthnetwork.earth2fa.config.SettingsHandler;
import net.earthnetwork.earth2fa.lang.Message;
import net.earthnetwork.earth2fa.utility.string.StringUtility;

public class Earth2FAPlugin extends JavaPlugin {
	
	private static Earth2FAPlugin plugin;
	private AuthHandler authHandler;
	private SettingsHandler settingsHandler;
	
	/**
	 * Used to log messages to console.
	 * 
	 *  @param message The image to be logged to console.
	 */
	public static void log(String message) {
        // Debug
		// plugin.pluginDebugger.debug(ChatColor.stripColor(message));
		
        message = StringUtility.translateColors(message);
        
        if (message.contains(ChatColor.COLOR_CHAR + "")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.getLastColors(message.substring(0, 2)) + "[" + plugin.getDescription().getName() + "] " + message);
        } else {
            plugin.getLogger().info(message);
        }
    }
	
	/**
	 * Used to get an instance of the plugin.
	 * 
	 * @return An instance of the plugin.
	 */
	public static Earth2FAPlugin getPlugin() {
		return plugin;
	}
	
	@Override
	public void onLoad() {
		plugin = this;
	}

	@Override
	public void onEnable() {
		Message.reload();
		authHandler = new AuthHandler();
		settingsHandler = new SettingsHandler();
		getAuthHandler().loadData();
		getSettingsHandler().reload();
	}
	
	@Override
	public void onDisable() {
		getAuthHandler().saveData();
	}

	public AuthHandler getAuthHandler() {
		return authHandler;
	}

	public SettingsHandler getSettingsHandler() {
		return settingsHandler;
	}

}