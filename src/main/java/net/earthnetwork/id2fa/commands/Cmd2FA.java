package net.earthnetwork.id2fa.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.earthnetwork.id2fa.ID2FAPlugin;
import net.earthnetwork.id2fa.auth.AuthHandler;

public class Cmd2FA implements CommandExecutor, TabCompleter {

	private AuthHandler authHandler;
	private ID2FAPlugin plugin = ID2FAPlugin.getPlugin();
	
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
		case 0:
			suggestions.add("help");
			suggestions.add("register");
			suggestions.add("connect");
			suggestions.add("unregister");
			break;
		case 1:
			suggestions.add("012345");
			break;
		default:
			break;
		}
		return suggestions;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (!sender.hasPermission("staff.use")) {
			
		}
		return true;
	}
}
