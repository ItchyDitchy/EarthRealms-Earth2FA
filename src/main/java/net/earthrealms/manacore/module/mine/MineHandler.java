package net.earthrealms.manacore.module.mine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.sk89q.worldedit.function.pattern.Pattern;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.account.object.Account;
import net.earthrealms.manacore.module.mine.command.MineCommand;

public class MineHandler {

	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
	
	private Map<Integer, Pattern> blocks = new HashMap<Integer, Pattern>();
	
	// Settings
	private int minHeight = 20;
	private int maxHeight = 79;
	
	public MineHandler() {
		new MineCommand();
	}
	
	public void reload() {
		
	}
	
	public void resetMine(SuperiorPlayer superiorPlayer) {
		
	}
	
	public Pattern getBlocks(UUID uuid) {
		Account account = plugin.getAccountHandler().getAccount(uuid);
		int rank = account.getRank();
		
		Pattern pattern = blocks.get(rank);
		
		if (pattern == null) {
			while(pattern == null) {
				rank -= 1;
				pattern = blocks.get(rank);
			}
			blocks.put(rank, pattern);
		}
		
		return pattern;
	}
	
	public Pattern getBlocks(SuperiorPlayer superiorPlayer) {
		return getBlocks(superiorPlayer.getUniqueId());
	}
	
	public int getMinHeight() {
		return minHeight;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
}
