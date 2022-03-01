package net.earthnetwork.id2fa.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import net.earthnetwork.id2fa.auth.AuthHandler;

public class PlayerListener {

	private AuthHandler authHandler;
	
	public PlayerListener(AuthHandler authHandler) {
		this.authHandler = authHandler;
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (!player.hasPermission("2fa.staff")) {
			return;
		}
		
		if (!this.getConfig().contains("" + player.getUniqueId())) {
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			GoogleAuthenticatorKey key = gAuth.createCredentials();

			player.sendMessage("§7Your §bGoogle Auth Code §7is §a" + key.getKey());
			player.sendMessage("§7You must enter this code in the Google Authenticator App before leaving the server.");

			this.getConfig().set("authcodes." + player.getUniqueId(), key.getKey());
			this.saveConfig();
		} else {
			authlocked.add(player.getUniqueId());
			player.sendMessage("§cPlease open the Google Authenticator App and provide the six digit code.");
		}
	}
	
}
