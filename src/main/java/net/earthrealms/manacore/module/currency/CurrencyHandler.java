package net.earthrealms.manacore.module.currency;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.module.currency.object.Currency;

public class CurrencyHandler {

	private ManaCorePlugin plugin = ManaCorePlugin.getPlugin();
	
	private Map<Currency, BigInteger> defaultValues = new HashMap<Currency, BigInteger>();
	private Map<String, String> formattedValues = new HashMap<String, String>();
	
	public CurrencyHandler() {
		
	}
	
	public void reload() {
		
	}
	
	public String getFormattedValue(BigInteger bigInteger) {
		for (String milestone : formattedValues.keySet()) {
			if (bigInteger.compareTo(new BigInteger(milestone)) == 1) {
				return formattedValues.get(milestone);
			}
		}
		return "";
	}
}
