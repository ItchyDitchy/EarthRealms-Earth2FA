package net.earthnetwork.earth2fa.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Sound;
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
	private Map<UUID, Long> warnCooldown = new HashMap<UUID, Long>();
	private List<UUID> hasLeft = new ArrayList<UUID>();
	
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
			authHandler.authenticatePlayer(player);
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
				tryWarnPlayer(player);
			}
		} catch (Exception e) {
			Message.CONNECT_FAILURE.send(player);
			tryWarnPlayer(player);
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void move(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
			tryWarnPlayer(player);
		}
	}

	@EventHandler
	public void blockbreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
			tryWarnPlayer(player);
		}
	}

	@EventHandler
	public void blockplace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
			tryWarnPlayer(player);
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
			tryWarnPlayer(player);
		}
	}
	
	public void tryWarnPlayer(Player player) {
		UUID uuid = player.getUniqueId();
		if (!warnCooldown.containsKey(uuid)) {
			warnCooldown.put(uuid, System.currentTimeMillis() + 1250);
			return;
		}
		if (warnCooldown.get(uuid) >= System.currentTimeMillis()) {
			return;
		}
		player.playSound(player.getEyeLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
		Message.AUTHENTICATION.send(player);
	}
}
