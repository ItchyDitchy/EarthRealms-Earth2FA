package net.earthrealms.manacore.module.account.object;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.account.exception.InvalidPlayerException;
import net.earthrealms.manacore.module.currency.object.Currency;
import net.earthrealms.manacore.utility.string.StringUtility;

public class Account {

	// Currency
	private List<Currency> currencies;
	
	// Status
	private String displayName;
	private boolean premium;
	private boolean operator;
	private int rank;
	private String prefix;
	private String title;
	private String suffix;
	private String nickColor;
	private String chatColor;

	// Maps & Lists
	private Map<String, Long> cooldowns;
	private Map<Integer, ItemStack[]> backpacks;
	private List<String> titles;

	// Constructors
	public Account() {
		
	}
	
	public Account(Player player) {
		createProfile(player.getUniqueId());
	}

	public Account(UUID uuid) {
		createProfile(uuid);
	}

	public Account(String username) throws InvalidPlayerException {
		Player player = Bukkit.getPlayer(username);
		if (player == null) {
			throw new InvalidPlayerException(username + " is not a player!");
		}
	}

	// Create Profile
	public Account createProfile(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		
		// Currency
		currencies = new ArrayList<Currency>();
		currencies.add(new Currency("orbs", "orbs", "o"));
		currencies.add(new Currency("beacons", "beacons", "b"));
		currencies.add(new Currency("credits", "credits", "c"));
		for (Currency currency : currencies) {
			currency.reset();
		}
		
		// Status
		displayName = player.getName();
		premium = false;
		operator = false;
		prefix = "1";
		title = "";
		suffix = "";
		nickColor = StringUtility.translateColor("&f");
		chatColor = StringUtility.translateColor("&f");
		
		// Maps & Lists
		cooldowns = new HashMap<String, Long>();
		backpacks = new HashMap<Integer, ItemStack[]>();
		titles = new ArrayList<String>();
		
		return this;
	}

	public void depositCurrency(String indentifier, BigInteger bigInteger) {
		ManaCorePlugin.getPlugin().getCurrencyHandler();
	}
	
	public void withdrawCurrency(String indentifier, BigInteger bigInteger) {
		
	}
	
	public void setCurrency(String indentifier, BigInteger bigInteger) {
		
	}
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}

	public boolean isOperator() {
		return operator;
	}

	public void setOperator(boolean operator) {
		this.operator = operator;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getNickColor() {
		return nickColor;
	}

	public void setNickColor(String nickColor) {
		this.nickColor = nickColor;
	}

	public String getChatColor() {
		return chatColor;
	}

	public void setChatColor(String chatColor) {
		this.chatColor = chatColor;
	}

	public Map<String, Long> getCooldowns() {
		return cooldowns;
	}

	public void setCooldowns(Map<String, Long> cooldowns) {
		this.cooldowns = cooldowns;
	}

	public Map<Integer, ItemStack[]> getBackpacks() {
		return backpacks;
	}

	public void setBackpacks(Map<Integer, ItemStack[]> backpacks) {
		this.backpacks = backpacks;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
}