package net.earthrealms.manacore.module.currency.object;

import java.math.BigInteger;

import net.earthrealms.manacore.ManaCorePlugin;

public class Currency {

	private String identifier = "vault";
	private String name = "money";
	private String symbol = "$";
	
	private BigInteger value = new BigInteger("0");
	
	public Currency(String identifier, String name, String symbol) {
		this.identifier = identifier;
		this.name = name;
		this.symbol = symbol;
	}
	
	public void deposit(BigInteger bigInteger) {
		value.add(bigInteger);
	}
	
	public void deposit(Integer integer) {
		deposit(BigInteger.valueOf(integer));
	}
	
	public void deposit(Long longValue) {
		deposit(BigInteger.valueOf(longValue));
	}
	
	public void withdraw(BigInteger bigInteger) {
		value.subtract(bigInteger);
	}
	
	public void withdraw(Integer integer) {
		withdraw(BigInteger.valueOf(integer));
	}
	
	public void withdraw(Long longValue) {
		withdraw(BigInteger.valueOf(longValue));
	}
	
	public void set(BigInteger bigInteger) {
		value = bigInteger;
	}
	
	public void set(Integer integer) {
		set(BigInteger.valueOf(integer));
	}
	
	public void set(Long longValue) {
		set(BigInteger.valueOf(longValue));
	}
	
	public void reset() {
		value = BigInteger.valueOf(0);
	}
	
	public String getRaw() {
		return value.toString();
	}
	
	public String getFixed() {
		return value.toString();
	}
	
	public String getCommas() {
		String string = value.toString();
		String.format( "{0:#,##0}", value);
		return string;
	}
	
	public String getFormatted() {
		String string = value.toString();
		ManaCorePlugin.getPlugin().getCurrencyHandler();
		return string;
	}
}
