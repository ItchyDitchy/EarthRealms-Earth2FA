package net.earthnetwork.id2fa.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import net.earthnetwork.id2fa.ID2FAPlugin;
import net.earthnetwork.id2fa.auth.AuthHandler;

public class PlayerListener implements Listener {

	private AuthHandler authHandler;
	
	private Map<UUID, String> tempKeys = new HashMap<UUID, String>();
	private ID2FAPlugin plugin = ID2FAPlugin.getPlugin();
	
	public PlayerListener(AuthHandler authHandler) {
		this.authHandler = authHandler;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (!player.hasPermission("2fa.staff")) {
			return;
		}
		
		if (authHandler.isPlayerAuthenticated(player)) {
			return;
		}
		
		if (!authHandler.hasCode(player)) {
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
            GoogleAuthenticatorKey key = gAuth.createCredentials();

            player.sendMessage("§7Your §bGoogle Auth Code §7is §a" + key.getKey());
            player.sendMessage("§7You must enter this code in the Google Authenticator App before leaving the server.");
		} else {
			authHandler.authenticatePlayer(player);
            player.sendMessage("§cPlease open the Google Authenticator App and provide the six digit code.");
		}
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		if (authHandler.isPlayerAuthenticated(player)) {
			try {
				Integer code = Integer.parseInt(message);
				if (authHandler.playerInputCode(player, code)) {
					authHandler.unauthenticatePlayer(player.getUniqueId());
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
		if (authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockbreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockplace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
		}
	}
}
