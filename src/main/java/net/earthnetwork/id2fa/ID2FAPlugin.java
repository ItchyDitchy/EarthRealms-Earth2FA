package net.earthnetwork.id2fa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.earthnetwork.id2fa.auth.AuthHandler;
import net.earthnetwork.id2fa.utility.StringUtility;

public class ID2FAPlugin extends JavaPlugin implements Listener {
	
	private static ID2FAPlugin plugin;
	private AuthHandler authHandler;
	
	public static void log(String message) {
        message = StringUtility.translateColors(message);
        if (message.contains(ChatColor.COLOR_CHAR + ""))
            Bukkit.getConsoleSender().sendMessage(ChatColor.getLastColors(message.substring(0, 2)) + "[" + plugin.getDescription().getName() + "] " + message);
        else
            plugin.getLogger().info(message);
    }
	
	public static ID2FAPlugin getPlugin() {
		return plugin;
	}
	
	@Override
	public void onLoad() {
		plugin = this;
	}

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		
		authHandler = new AuthHandler(this);
		authHandler.loadData();
	}
	
	@Override
	public void onDisable() {
		authHandler.saveData();
	}

}