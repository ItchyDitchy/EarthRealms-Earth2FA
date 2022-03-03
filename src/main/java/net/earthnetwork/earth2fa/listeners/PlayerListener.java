package net.earthnetwork.earth2fa.listeners;

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

import net.earthnetwork.earth2fa.Earth2FAPlugin;
import net.earthnetwork.earth2fa.auth.AuthHandler;
import net.earthnetwork.earth2fa.lang.Message;

public class PlayerListener implements Listener {

	private AuthHandler authHandler;
	
	private Map<UUID, String> tempKeys = new HashMap<UUID, String>();
	private Earth2FAPlugin plugin = Earth2FAPlugin.getPlugin();
	
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
			return;
		}
		
		Message.AUTHENTICATION.send(player);
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		if (authHandler.isPlayerAuthenticated(player)) {
			return;
		}
		
		String message = event.getMessage();
		
		try {
			Integer code = Integer.parseInt(message);
			if (authHandler.playerInputCode(player, code)) {
				authHandler.authenticatePlayer(player.getUniqueId());
				Message.CONNECT_SUCCESS.send(player);
			} else {
				Message.CONNECT_FAILURE.send(player);
			}
		} catch (Exception e) {
			Message.CONNECT_FAILURE.send(player);
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void move(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockbreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockplace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toUpperCase().startsWith("2FA")) {
			return;
		}
		Player player = event.getPlayer();
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
		}
	}
}
