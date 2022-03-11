package net.earthrealms.manacore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import net.earthrealms.manacore.module.account.AccountHandler;
import net.earthrealms.manacore.module.currency.CurrencyHandler;
import net.earthrealms.manacore.module.mine.MineHandler;
import net.earthrealms.manacore.utility.string.StringUtility;

public class ManaCorePlugin extends JavaPlugin {

	private static ManaCorePlugin plugin;
	
	private AccountHandler accountHandler;
	private CurrencyHandler currencyHandler;
	private MineHandler mineHandler;
	
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
	
	public static ManaCorePlugin getPlugin() {
		return plugin;
	}
	
	@Override
	public void onLoad() {
		plugin = this;
	}
	
	@Override
	public void onEnable() {
		accountHandler = new AccountHandler();
		currencyHandler = new CurrencyHandler();
		mineHandler = new MineHandler();
		
		reload();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void reload() {
		accountHandler.reload();
		
		currencyHandler.reload();
		mineHandler.reload();
		
	}
	public AccountHandler getAccountHandler() {
		return accountHandler;
	}
	
	public CurrencyHandler getCurrencyHandler() {
		return currencyHandler;
	}
	
	public MineHandler getMineHandler() {
		return mineHandler;
	}
}
