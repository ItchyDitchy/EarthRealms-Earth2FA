package net.earthrealms.manacore.module.account;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.account.listener.OnlinePlayerListener;
import net.earthrealms.manacore.module.account.object.Account;
import net.earthrealms.manacore.module.mine.listener.PlayerListener;

public class AccountHandler {

	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
	
	private Map<UUID, Account> accounts;
	private Map<String, UUID> nameIDs;
	
	public AccountHandler() {
		if (Bukkit.getServer().getOnlineMode()) {
			new OnlinePlayerListener();
		} else {
			new PlayerListener();
		}
		reload();
	}
	
	public void reload() {
		if (accounts != null) {
			saveAccounts();
		}
		loadAccounts();
	}
	
	public void loadAccounts() {
		
		accounts = new HashMap<UUID, Account>();
		nameIDs = new HashMap<String, UUID>();
		
        final File folder = new File(plugin.getDataFolder(), "database/account");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        String[] fAccounts = folder.list();
        
		for (int index = 0; index < fAccounts.length; index++) {
			String fileName = fAccounts[index];
			File file = new File(folder, fileName);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			Account account = new Account();
			
			// Status
			account.setDisplayName(config.getString("displayName"));
			account.setPremium(config.getBoolean("premium"));
			account.setOperator(config.getBoolean("operator"));
			
			account.setPrefix(config.getString("prefix"));
			account.setTitle(config.getString("title"));
			account.setSuffix(config.getString("suffix"));
			account.setNickColor(config.getString("nickColor"));
			account.setChatColor(config.getString("chatColor"));
			
			// Maps & Lists
			Map<String, Long> cooldowns = new HashMap<String, Long>();
			if (config.isSet("cooldowns") && config.isConfigurationSection("cooldowns")) {
				for (String id : config.getConfigurationSection("cooldowns").getKeys(false)) {
					cooldowns.put(id, config.getLong("cooldowns." + id));
				}
			}
			Map<Integer, ItemStack[]> backpacks = new HashMap<Integer, ItemStack[]>();
			if (config.isSet("backpacks") && config.isConfigurationSection("backpacks")) {
				for (int backpackNumber : account.getBackpacks().keySet()) {
					backpacks.put(backpackNumber, ((List<ItemStack>) config.getList("backpacks." + backpackNumber)).toArray(new ItemStack[0]));
				}
			}
			account.setCooldowns(cooldowns);
			account.setBackpacks(backpacks);
			account.setTitles(config.getStringList("titles"));
			
			UUID uuid = UUID.fromString(fileName.replaceFirst(".yml", ""));
			saveAccount(uuid, account);
		}
	}
	
	public void saveAccounts() {
        final File folder = new File(plugin.getDataFolder(), "profiles/accounts");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        Long time = System.currentTimeMillis();
        plugin.getLogger().info("Saving " + accounts.keySet().size() + " accounts.");
        
        if (accounts.keySet().size() == 0) {
        	return;
        }
        
		for (UUID uuid : accounts.keySet()) {
			Account account = accounts.get(uuid);
			
			File file = new File(folder, uuid.toString() + ".yml");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			// Status
			config.set("displayName", account.getDisplayName());
			config.set("premium", account.isPremium());
			config.set("operator", account.isOperator());
			config.set("prefix", account.getPrefix());
			config.set("title", account.getTitle());
			config.set("suffix", account.getSuffix());
			config.set("nickColor", account.getNickColor());
			config.set("chatColor", account.getChatColor());
			
			// Maps & Lists
			config.set("cooldowns", null);
			for (String id : account.getCooldowns().keySet()) {
				config.set("cooldowns." + id, account.getCooldowns().get(id));
			}
			config.set("backpacks", null);
			for (int backpackNumber : account.getBackpacks().keySet()) {
				config.set("backpacks." + backpackNumber, account.getBackpacks().get(backpackNumber));
			}
			config.set("titles", account.getTitles());
			
			try {
				config.save(file);
			} catch (IOException exception) {
				 exception.printStackTrace();
			}
		}
		plugin.getLogger().info("It took " + (System.currentTimeMillis() - time) + "ms to save " + accounts.keySet().size() + " accounts.");
	}
	
	public Account getAccount(UUID uuid) {
		return accounts.get(uuid);
	}
	
	public Map<UUID, Account> getAccounts() {
		return accounts;
	}
	
	public void saveAccount(UUID uuid, Account account) {
		accounts.put(uuid, account);
		nameIDs.put(account.getDisplayName().toUpperCase(), uuid);
	}
	
	public UUID getUUID(String name) {
		name = name.toUpperCase();
		if (nameIDs.containsKey(name)) {
			return nameIDs.get(name);
		}
		return null;
	}
}