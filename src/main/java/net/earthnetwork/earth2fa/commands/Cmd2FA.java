package net.earthnetwork.earth2fa.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import net.earthnetwork.earth2fa.Earth2FAPlugin;
import net.earthnetwork.earth2fa.auth.AuthHandler;
import net.earthnetwork.earth2fa.lang.Message;

public class Cmd2FA implements CommandExecutor, TabCompleter {

	private AuthHandler authHandler;
	private Earth2FAPlugin plugin = Earth2FAPlugin.getPlugin();
	private Map<UUID, String> temporaryKeys = new HashMap<UUID, String>();
	
	public Cmd2FA(AuthHandler authHandler) {
		this.authHandler = authHandler;
		
		PluginCommand pluginCommand = plugin.getCommand("2FA");
		pluginCommand.setExecutor(this);
		pluginCommand.setTabCompleter(this);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> suggestions = new ArrayList<String>();
		switch (args.length) {
		case 1:
			suggestions.add("help");
			suggestions.add("register");
			suggestions.add("connect");
			suggestions.add("unregister");
			suggestions.add("reload");
			suggestions.add("reset");
			break;
		case 2:
			suggestions.add("012345");
			break;
		default:
			break;
		}
		return suggestions;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean isConsole = !(sender instanceof Player);
		
		if (!sender.hasPermission("staff.use")) {
			Message.NO_PERMISSION.send(sender);
			return true;
		}
		
		Player player = null;
		if (!isConsole) {
			player = (Player) sender;
		}
		switch (args.length) {
		case 1:
			if (args[0].equalsIgnoreCase("register")) {
				if (isConsole || !sender.hasPermission("earth2fa.auth")) {
					Message.NO_PERMISSION.send(sender);
					break;
				}
				
				if (authHandler.hasCode(player)) {
					break;
				}
				GoogleAuthenticator gAuth = new GoogleAuthenticator();
	            GoogleAuthenticatorKey key = gAuth.createCredentials();
	            temporaryKeys.put(player.getUniqueId(), key.getKey());
	            
				Message.FIRST_AUTHENTICATION.send(sender, key.getKey());
				break;
			}
			if (args[0].equalsIgnoreCase("connect")) {
				if (isConsole || !sender.hasPermission("earth2fa.auth")) {
					Message.NO_PERMISSION.send(sender);
					break;
				}
				Message.CONNECT_USAGE.send(player);
				break;
			}
			if (args[0].equalsIgnoreCase("unregister")) {
				if (isConsole || !sender.hasPermission("earth2fa.unregister")) {
					Message.NO_PERMISSION.send(sender);
					break;
				}
				if (!authHandler.hasCode(player)) {
					Message.NOT_REGISTERED.send(player);
				}
				authHandler.unregisterPlayer(player);
				Message.UNREGISTER_ALERT.send(plugin.getServer().getConsoleSender());
				break;
			}
			if (args[0].equalsIgnoreCase("reset")) {
				Message.RESET_USAGE.send(sender);
				break;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				Message.RELOAD_PRE.send(sender);
				Long preTime = System.currentTimeMillis();
				Message.reload();
				Message.RELOAD_POST.send(sender, System.currentTimeMillis() - preTime);
				break;
			}
			Message.HELP_GUIDE.send(sender);
			break;
		case 2:
			if (args[0].equalsIgnoreCase("reset")) {
				if (!isConsole) {
					Message.NO_PERMISSION.send(sender);
					break;
				}
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
				if (offlinePlayer == null) {
					Message.RESET_USAGE.send(sender);
					break;
				}
				authHandler.unregisterPlayer(offlinePlayer);
				Message.UNREGISTER_ALERT.send(sender);
				break;
			}
			if (args[0].equalsIgnoreCase("connect")) {
				if (isConsole || !sender.hasPermission("earth2fa.auth")) {
					Message.NO_PERMISSION.send(sender);
					break;
				}
				
				if (!temporaryKeys.containsKey(player.getUniqueId())) {
					Message.NO_REGISTRATION.send(sender);
					break;
				}
				String secretkey = temporaryKeys.get(player.getUniqueId());

				GoogleAuthenticator gAuth = new GoogleAuthenticator();
				
				if (args[1].length() > 6) {
					Message.CONNECT_FAILURE.send(player);
					break;
				}
				
				int code = 0;
				
				try {
					code = Integer.parseInt(args[1]);
				} catch (NumberFormatException exception) {
					Message.CONNECT_FAILURE.send(player);
					break;
				}
				
				boolean codeisvalid = gAuth.authorize(secretkey, code);
				
				if (codeisvalid) {
					authHandler.registerPlayer(player, secretkey);
					authHandler.authenticatePlayer(player);
					Message.CONNECT_SUCCESS.send(sender);
					break;
				}
				Message.CONNECT_FAILURE.send(player);
				break;
			}
		default:
			Message.HELP_GUIDE.send(sender);
			break;
		}
		return true;
	}
}
