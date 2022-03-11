package net.earthrealms.manacore.module.account.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.account.object.Account;

public class OnlinePlayerListener implements Listener {

	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();

	public OnlinePlayerListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		if (!plugin.getAccountHandler().getAccounts().containsKey(event.getPlayer().getUniqueId())) {
			plugin.getAccountHandler().saveAccount(event.getPlayer().getUniqueId(), new Account(event.getPlayer()));
		}
	}
}
