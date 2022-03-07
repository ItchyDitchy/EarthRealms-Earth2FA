package net.earthnetwork.earth2fa.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.earthnetwork.earth2fa.Earth2FAPlugin;
import net.earthnetwork.earth2fa.auth.AuthHandler;
import net.earthnetwork.earth2fa.lang.Message;
import net.kitesoftware.board.KiteBoardPlugin;
import net.kitesoftware.board.group.GroupType;

public class PlayerListener implements Listener {

	private AuthHandler authHandler;
	
	private Map<UUID, String> tempKeys = new HashMap<UUID, String>();
	private Map<UUID, Long> warnCooldown = new HashMap<UUID, Long>();
	private Map<UUID, Boolean> authenticated = new HashMap<UUID, Boolean>();
	
	private Earth2FAPlugin plugin = Earth2FAPlugin.getPlugin();
	private KiteBoardPlugin kiteBoardPlugin;
	
	public PlayerListener(AuthHandler authHandler) {
		this.authHandler = authHandler;
		kiteBoardPlugin = (KiteBoardPlugin) plugin.getServer().getPluginManager().getPlugin("KiteBoard");
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		new BukkitRunnable() {
            public void run() {
            	giveEffects();
            }
        }.runTaskTimer(plugin, 0, 20*3);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (!player.hasPermission("2fa.staff")) {
			return;
		}
		
		if (!authHandler.hasCode(player)) {
			return;
		}
		
		if (authHandler.isPlayerAuthenticated(player)) {
			authHandler.authenticatePlayer(player);
			authenticated.put(player.getUniqueId(), true);
			return;
		}

		authenticated.put(player.getUniqueId(), false);
		Message.AUTHENTICATION.send(player);
		kiteBoardPlugin.getUserManager().getUser(player).setGroupEnabled(GroupType.SCOREBOARD, false);
    	giveEffects();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (authenticated.get(event.getPlayer().getUniqueId())) {
			authenticated.remove(event.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("2fa.staff")) {
			return;
		}
		
		if (!authHandler.hasCode(player)) {
			return;
		}
		
		if (authenticated.containsKey(player.getUniqueId()) && authenticated.get(player.getUniqueId())) {
			return;
		}
		if (authHandler.isPlayerAuthenticated(player)) {
			return;
		}
		
		String message = event.getMessage();
		
		try {
			Integer code = Integer.parseInt(message);
			if (authHandler.playerInputCode(player, code)) {
				authHandler.authenticatePlayer(player.getUniqueId());
				Message.CONNECT_SUCCESS.send(player);
				authenticated.put(player.getUniqueId(), true);
				for (PotionEffectType potionEffectType : plugin.getSettingsHandler().getPotionEffects()) {
					player.removePotionEffect(potionEffectType);
				}
				kiteBoardPlugin.getUserManager().getUser(player).setGroupEnabled(GroupType.SCOREBOARD, true);
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

		if (!player.hasPermission("2fa.staff")) {
			return;
		}
		
		if (!authHandler.hasCode(player)) {
			return;
		}
		
		if (authenticated.containsKey(player.getUniqueId()) && authenticated.get(player.getUniqueId())) {
			return;
		}
		
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
			tryWarnPlayer(player);
		}
	}

	@EventHandler
	public void blockbreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("2fa.staff")) {
			return;
		}
		
		if (!authHandler.hasCode(player)) {
			return;
		}
		
		if (authenticated.containsKey(player.getUniqueId()) && authenticated.get(player.getUniqueId())) {
			return;
		}
		
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
			tryWarnPlayer(player);
		} 
	}

	@EventHandler
	public void blockplace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("2fa.staff")) {
			return;
		}
		
		if (!authHandler.hasCode(player)) {
			return;
		}
		
		if (authenticated.containsKey(player.getUniqueId()) && authenticated.get(player.getUniqueId())) {
			return;
		}
		
		if (!authHandler.isPlayerAuthenticated(player)) {
			event.setCancelled(true);
			tryWarnPlayer(player);
		}
	}
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		if (!player.hasPermission("2fa.staff")) {
			return;
		}
		
		if (!authHandler.hasCode(player)) {
			return;
		}
		
		if (authenticated.containsKey(player.getUniqueId()) && authenticated.get(player.getUniqueId())) {
			return;
		}
		
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
		warnCooldown.put(uuid, System.currentTimeMillis() + 1250);
		player.playSound(player.getEyeLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
		Message.AUTHENTICATION.send(player);
	}
	
	public void giveEffects() {
		for (UUID uuid : authenticated.keySet()) {
			if (!authenticated.get(uuid)) {
				Player player = Bukkit.getPlayer(uuid);
				for (PotionEffectType potionEffectType : plugin.getSettingsHandler().getPotionEffects()) {
					player.addPotionEffect(new PotionEffect(potionEffectType, 20*5, 10), true);
				}
			}
		}
	}
}
