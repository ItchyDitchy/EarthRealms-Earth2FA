package net.earthrealms.manacore.module.mine.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;

import net.earthrealms.manacore.ManaCorePlugin;

public class MineCommand implements CommandExecutor, TabCompleter {

	private int size = 9;
	private WorldEditPlugin worldEdit = WorldEditPlugin.getInstance();
	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();

	private Map<UUID, RandomPattern> mineBlocks = new HashMap<UUID, RandomPattern>();

	public MineCommand() {
		plugin.getCommand("tmine").setExecutor(this);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;
		Island island = SuperiorSkyblockAPI.getPlayer(player).getIsland();

		if (island == null) {
			return false;
		}

		if (args.length == 0) {

		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("Reset")) {
				resetMine(player, island);
			} else if (args[0].equalsIgnoreCase("Increase")) {
				increaseSize(1);
				resetMine(player, island);
			} else if (args[0].equalsIgnoreCase("Decrease")) {
				decreaseSize(1);
				resetMine(player, island);
			}
		}
		return true;
	}

	public void increaseSize(int sizeIncrease) {
		sizeIncrease *= 2;
		if (size + sizeIncrease >= 177) {
			size = 177;
		} else {
			size += sizeIncrease;
		}
	}

	public void setSize(int size) {

	}

	public void decreaseSize(int sizeDecrease) {
		sizeDecrease *= 2;
		if (size - sizeDecrease <= 9) {
			size = 9;
			return;
		} else {
			size -= sizeDecrease;
		}
	}

	public void resetMine(Player player, Island island) {
		RandomPattern pattern = new RandomPattern();

		BlockState stone = BukkitAdapter.adapt(Material.STONE.createBlockData());

		pattern.add(stone, 1);

		mineBlocks.put(player.getUniqueId(), pattern);

		int halfSize = (size-1)/2;
		
		int delay = 0;
		for (int i = 20; i <= 79; i++) {
			delay++;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

	            @Override
	            public void run(){

			EditSession editSession = worldEdit.getWorldEdit().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(player.getWorld()), -1);
			Location center = island.getCenter(Environment.NORMAL);
			player.sendMessage(center.toString());
			Region region = new CuboidRegion(
					BlockVector3.at(center.getX() + halfSize, 20, center.getZ() + halfSize),
					BlockVector3.at(center.getX() - halfSize, 79, center.getZ() - halfSize));
			try {
				editSession.setBlocks(region, mineBlocks.get(player.getUniqueId()));
			} catch (MaxChangedBlocksException ex) {
				ex.printStackTrace();
			}
			editSession.close();
	            }
	        }, delay*2);
		}
	}

}
