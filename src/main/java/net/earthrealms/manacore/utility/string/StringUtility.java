package net.earthrealms.manacore.utility.string;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import net.md_5.bungee.api.ChatColor;

public class StringUtility {

	private static final String k = "K";
	private static final String m = "M";
	private static final String b = "B";
	private static final String t = "T";
	private static final String q = "Q";

	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)(&|" + ChatColor.COLOR_CHAR + ")[0-9A-FK-OR]");
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(&|§)(\\{HEX:([0-9A-Fa-f]*)\\})");
	private static final Pattern NUMBER_PATTERN = Pattern.compile("^[a-z]{2}[_|-][A-Z]{2}$");
	private static Pattern DECIMAL_PATTERN;

	private static DecimalFormat numberFormatter;
	private static char decimalSeparator;
	private static SimpleDateFormat dateFormatter;

	public static String fixedNumber(double number) {
		return String.valueOf((long)number);
	}
	
	public static String fixedNumber(long number) {
		return String.valueOf(number);
	}
	
	public static String format(double d) {
		return format(BigDecimal.valueOf(d));
	}

	public static String format(BigInteger bigInteger) {
		return format(new BigDecimal(bigInteger));
	}

	public static String format(BigDecimal bigDecimal) {
		String s = numberFormatter.format(bigDecimal);

		Matcher matcher;

		if (s.endsWith(decimalSeparator + "00")) {
			return s.replace(decimalSeparator + "00", "");
		} else if ((matcher = DECIMAL_PATTERN.matcher(s)).matches()) {
			return s.replaceAll(Pattern.quote(decimalSeparator + "") + "(\\d)0", decimalSeparator + matcher.group(2));
		}

		return s;
	}

	public static String commasNumber(double value) {
		String pattern = "###,###.###";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);

		String format = decimalFormat.format(value);

		return format;
	}

	public static String roundOff(long l, int i) {
		NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		format.setMaximumFractionDigits(i);
		format.setMinimumFractionDigits(0);
		return format.format(l);
	}

	public static String formattedNumber(double d) {
		if (d < 1000) {
			return roundOff(d, 0);
		}
		if (d < 1000000L) {
			return roundOff(d / 1000l, 2) + k;
		}
		if (d < 1000000000L) {
			return roundOff(d / 1000000l, 2) + m;
		}
		if (d < 1000000000000L) {
			return roundOff(d / 1000000000l, 2) + b; 
		}
		if (d < 1000000000000000L) {
			return roundOff(d / 1000000000000l, 2) + t; 
		}
		if (d < 1000000000000000000L) {
			return roundOff(d / 1000000000000000l, 2) + q; 
		}
		return roundOff(d, 0);
	}

	public static String formattedNumber(Long l) {
		Double d = (double) l;
		if (d < 1000) {
			return roundOff(d, 0);
		}
		if (d < 1000000L) {
			return roundOff(d / 1000l, 2) + k; 
		}
		if (d < 1000000000L) {
			return roundOff(d / 1000000l, 2) + m; 
		}
		if (d < 1000000000000L) {
			return roundOff(d / 1000000000l, 2) + b; 
		}
		if (d < 1000000000000000L) {
			return roundOff(d / 1000000000000l, 2) + t; 
		}
		if (d < 1000000000000000000L) {
			return roundOff(d / 1000000000000000l, 2) + q;
		}
		return roundOff(d, 0);
	}

	public static String roundOff(double d, int i) {
		NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		format.setMaximumFractionDigits(i);
		format.setMinimumFractionDigits(0);
		return format.format(d);
	}
	
	public static String entityFormat(String string) {
		String[] array = string.replace("_", " ").split(" "); string = "";
		for (int i = 0; i<array.length; i++) {
			string += " " + array[i].substring(0, 1).toUpperCase() + array[i].substring(1).toLowerCase();
		}
		return string.replaceFirst(" ", "");
	}

	public static String timeFormat(long millis) {
		String format = "";
		long seconds = millis / 1000L;
		long minutes = seconds / 60L;
		long hours = minutes / 60L;
		long days = hours / 24L;
		long months = days / 30L;
		long years = months / 12L;
		seconds -= minutes * 60L;
		minutes -= hours * 60L;
		hours -= days * 24L;
		days -= months * 30L;
		months -= years * 12L;
		if (years > 0L)
			format = format + years + "y"; 
		if (months > 0L) {
			if (!format.isEmpty())
				format = format + " "; 
			format = format + months + "mo";
		} 
		if (days > 0L) {
			if (!format.isEmpty())
				format = format + " "; 
			format = format + days + "d";
		} 
		if (hours > 0L) {
			if (!format.isEmpty())
				format = format + " "; 
			format = format + hours + "h";
		} 
		if (minutes > 0L) {
			if (!format.isEmpty())
				format = format + " "; 
			format = format + minutes + "m";
		} 
		if (seconds > 0L) {
			if (!format.isEmpty())
				format = format + " "; 
			format = format + seconds + "s";
		} 
		return format;
	}
	
	public static String centerText(String text){
		return StringUtils.center(text, 320);
	}
	
	public static String translateColor(String string) {
		return translateColors(string);
	}
	
	public static String translateColors(String string) {
        String output = ChatColor.translateAlternateColorCodes('&', string);

//        if (ServerVersion.isLessThan(ServerVersion.v1_16))
//            return output;

        while (true) {
            Matcher matcher = HEX_COLOR_PATTERN.matcher(output);

            if (!matcher.find())
                break;

            output = matcher.replaceFirst(parseHexColor(matcher.group(3)));
        }

        return output;
    }
	
	private static String parseHexColor(String hexColor) {
        if (hexColor.length() != 6 && hexColor.length() != 3)
            return hexColor;

        StringBuilder magic = new StringBuilder(ChatColor.COLOR_CHAR + "x");
        int multiplier = hexColor.length() == 3 ? 2 : 1;

        for (char ch : hexColor.toCharArray()) {
            for (int i = 0; i < multiplier; i++)
                magic.append(ChatColor.COLOR_CHAR).append(ch);
        }

        return magic.toString();
    }
	
	public static String replaceArgs(String msg, Object... objects) {
		if (msg == null)
			return null;

		for (int i = 0; i < objects.length; i++) {
			String objectString = objects[i] instanceof BigDecimal ?
					format((BigDecimal) objects[i]) : objects[i].toString();
			msg = msg.replace("{" + i + "}", objectString);
		}

		return msg;
	}
}
