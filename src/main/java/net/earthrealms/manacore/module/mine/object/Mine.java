package net.earthrealms.manacore.module.mine.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.api.exception.InvalidDirectionException;

public class Mine {

	private UUID mineID;
	private UUID owner;
	private UUID coOwner;
	
	private List<UUID> whitelistedMembers;
	private List<UUID> bannedMembers;
	private List<UUID> priorityMembers;
	
	private int blocksBroken = 0;
	private int maxBlocks = 0;
	
	private int mineSize = 9;
	private boolean open = true;
	
	private int maxMiners = 0;
	private List<UUID> miners = new ArrayList<UUID>();
	
	private long cooldown = System.currentTimeMillis();
	
	private Location spawn;
	
	public Mine(UUID uuid, int size) {
		this.mineID = uuid;
		this.mineSize = size;
	}

	public void teleport(UUID uuid) {
		teleport(Bukkit.getPlayer(uuid));
	}
	
	public void teleport(Player player) {
		
	}
	
	public void teleport(SuperiorPlayer superiorPlayer) {
		teleport(superiorPlayer.asPlayer());
	}
	
	public void reset() {
		RegenDirection.valueOf("");
	}

	public void reset(String direction) throws InvalidDirectionException {
		direction = direction.toUpperCase();

		SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(mineID);
		Island island = superiorPlayer.getIsland();

		if (island == null) {
			return;
		}

		int halfSize = (mineSize-1)/2;
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
		Location center = island.getCenter(Environment.NORMAL);

		ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
		int delay = 0;

		int x1 = (int) (center.getX() - halfSize);
		int x2 = (int) (center.getX() + halfSize);
		int y1 = plugin.getMineHandler().getMinHeight();
		int y2 = plugin.getMineHandler().getMaxHeight();
		int z1 = (int) (center.getZ() - halfSize);
		int z2 = (int) (center.getZ() + halfSize);
		
		Pattern pattern = ManaCorePlugin.getPlugin().getMineHandler().getBlocks(superiorPlayer);
		
		switch (direction) {
		case "INSTANT":
			Region region = new CuboidRegion(
					BlockVector3.at(x1, y1, z1),
					BlockVector3.at(x2, y2, z2));

			editSession.setBlocks(region, pattern);
			editSession.close();
			break;
		case "UP":
			for (int i = y1; i <= y2; i++) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						Region region = new CuboidRegion(
								BlockVector3.at(x1, layer, z1),
								BlockVector3.at(x2, layer, z2));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case "DOWN":
			for (int i = y2; i >= y1; i--) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						Region region = new CuboidRegion(
								BlockVector3.at(x1, layer, z1),
								BlockVector3.at(x2, layer, z2));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case "NORTH":
			for (int i = z2; i <= z1; i--) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						Region region = new CuboidRegion(
								BlockVector3.at(x1, y1, layer),
								BlockVector3.at(x2, y2, layer));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case "EAST":
			for (int i = x1; i <= x2; i++) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						Region region = new CuboidRegion(
								BlockVector3.at(layer, y1, z1),
								BlockVector3.at(layer, y2, z2));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case "SOUTH":
			for (int i = z1; i <= z2; i++) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						Region region = new CuboidRegion(
								BlockVector3.at(x1, y1, layer),
								BlockVector3.at(x2, y2, layer));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		case "WEST":
			for (int i = x2; i <= x1; i--) {
				final int layer = i;
				delay++;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

					@Override
					public void run(){
						Region region = new CuboidRegion(
								BlockVector3.at(layer, y1, z1),
								BlockVector3.at(layer, y2, z2));

						editSession.setBlocks(region, pattern);
						editSession.close();
					}
				}, delay*2);
			}
			break;
		default:
			throw new InvalidDirectionException("");
		}
	}

	public void setBlocks(Map<Material, Double> blocks) {

	}

	public void clearBlocks() {

	}

	public void generateBlocks() {

	}

	public void generateOutline() {
		SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(mineID);
		Island island = superiorPlayer.getIsland();


		int halfSize = ((mineSize-1)/2) + 2;
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(SuperiorSkyblockAPI.getIslandsWorld(island, Environment.NORMAL)), -1);
		Location center = island.getCenter(Environment.NORMAL);
		Region region = new CuboidRegion(BlockVector3.at(center.getX() + halfSize, 20, center.getZ() + halfSize), BlockVector3.at(center.getX() - halfSize, 79, center.getZ() - halfSize));

		RandomPattern pattern = new RandomPattern();

		pattern.add(BukkitAdapter.adapt(Material.BEDROCK.createBlockData()), 1);

		editSession.makeWalls(region, pattern);
		editSession.close();
	}

	public void generateDefaultMine() {

	}

	public void generateSchematic() {

	}

	public void getOwner() {

	}
}
