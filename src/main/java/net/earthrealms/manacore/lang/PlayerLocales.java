package net.earthrealms.manacore.lang;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import com.google.common.base.Preconditions;

import net.earthrealms.manacore.ManaCorePlugin;

public final class PlayerLocales {

	private static final ManaCorePlugin plugin = ManaCorePlugin.getPlugin();

    private static final Pattern RTL_LOCALE_PATTERN = Pattern.compile(
            "^(ar|dv|he|iw|fa|nqo|ps|sd|ug|ur|yi|.*[-_](Arab|Hebr|Thaa|Nkoo|Tfng))(?!.*[-_](Latn|Cyrl)($|-|_))($|-|_)");
    private static final Pattern LOCALE_PATTERN = Pattern.compile("^[a-z]{2}[_|-][A-Z]{2}$");

    private static final Set<Locale> locales = new HashSet<>();
    private static final Set<UUID> noInteractMessages = new HashSet<>();
    private static final Set<UUID> noSchematicMessages = new HashSet<>();
    private static java.util.Locale defaultLocale = null;

    private PlayerLocales() {

    }

    public static Locale getLocale(CommandSender sender) {
    	return defaultLocale;
    }

    public static java.util.Locale getLocale(String str) throws IllegalArgumentException {
        str = str.replace("_", "-");

        Preconditions.checkArgument(LOCALE_PATTERN.matcher(str).matches(), "String " + str + " is not a valid language.");

        String[] numberFormatSections = str.split("-");

        return new java.util.Locale(numberFormatSections[0], numberFormatSections[1]);
    }

    public static boolean isRightToLeft(Locale locale) {
        return RTL_LOCALE_PATTERN.matcher(locale.getLanguage()).matches();
    }

    public static void clearLocales() {
        locales.clear();
    }

    public static void registerLocale(java.util.Locale locale) {
        if (locales.isEmpty())
            defaultLocale = locale;
        locales.add(locale);
    }

    public static java.util.Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static void setDefaultLocale(Locale defaultLocale) {
        PlayerLocales.defaultLocale = defaultLocale;
    }

    public static boolean isValidLocale(java.util.Locale locale) {
        return locales.contains(locale);
    }

}
