package net.earthnetwork.id2fa;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import net.earthnetwork.id2fa.utility.StringUtility;

public class ID2FAPlugin extends JavaPlugin implements Listener {
	
	private static ID2FAPlugin plugin;
	
	public static void log(String message) {
        //plugin.pluginDebugger.debug(ChatColor.stripColor(message));
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
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		if (authlocked.contains(player.getUniqueId())) {
			try {
				Integer code = Integer.parseInt(message);
				if (playerInputCode(player, code)) {
					authlocked.remove(player.getUniqueId());
					player.sendMessage("§a*Access Granted* §bWelcome to the server!");
				} else {
					player.sendMessage("§cIncorrect or expired code ** A code will only contain numbers **");

				}
			} catch (Exception e) {
				player.sendMessage("§cIncorrect or expired code ** A code will only contain numbers **");
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void move(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (authlocked.contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockbreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (authlocked.contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockplace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (authlocked.contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}

}