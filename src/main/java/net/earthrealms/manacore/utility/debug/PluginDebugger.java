package net.earthrealms.manacore.utility.debug;

import java.util.regex.Pattern;

import net.earthrealms.manacore.ManaCorePlugin;

public final class PluginDebugger {

    private static final ManaCorePlugin plugin = ManaCorePlugin.getPlugin();

    private static boolean debugMode = false;
    private static Pattern debugFilter = null;

    private PluginDebugger() {

    }

    public static void debug(String message) {
        if (debugMode && (debugFilter == null || debugFilter.matcher(message.toUpperCase()).find()))
            plugin.getLogger().info("[DEBUG] " + message);
    }

    public static void debug(Throwable error) {
        //plugin.pluginDebugger.debug(error);
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void toggleDebugMode() {
        debugMode = !debugMode;
    }

    public static void setDebugFilter(String debugFilter) {
        if (debugFilter.isEmpty())
            PluginDebugger.debugFilter = null;
        else
            PluginDebugger.debugFilter = Pattern.compile(debugFilter.toUpperCase());
    }


}