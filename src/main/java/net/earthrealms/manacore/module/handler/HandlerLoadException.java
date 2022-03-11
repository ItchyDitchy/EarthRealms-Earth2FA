package net.earthrealms.manacore.module.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import net.earthrealms.manacore.ManaCorePlugin;
import net.earthrealms.manacore.utility.debug.PluginDebugger;

@SuppressWarnings("all")
public class HandlerLoadException extends Exception {

    private static final ManaCorePlugin plugin = ManaCorePlugin.getPlugin();

    private final ErrorLevel errorLevel;

    public HandlerLoadException(String message, ErrorLevel errorLevel) {
        super(message == null ? "" : message);
        this.errorLevel = errorLevel;
    }

    public HandlerLoadException(Throwable cause, ErrorLevel errorLevel) {
        super(cause);
        this.errorLevel = errorLevel;
    }

    public HandlerLoadException(Throwable cause, String message, ErrorLevel errorLevel) {
        super(message, cause);
        this.errorLevel = errorLevel;
    }

    public static boolean handle(HandlerLoadException ex) {
        PluginDebugger.debug(ex);
        ex.printStackTrace();

        if (ex.getErrorLevel() == ErrorLevel.SERVER_SHUTDOWN) {
            Bukkit.shutdown();
            return false;
        }

        return true;
    }

    public ErrorLevel getErrorLevel() {
        return errorLevel;
    }

    @Override
    public void printStackTrace() {
        if (getErrorLevel() == ErrorLevel.CONTINUE) {
            super.printStackTrace();
            return;
        }

        StringWriter stackTrace = new StringWriter();
        super.printStackTrace(new PrintWriter(stackTrace));

        List<String> messageLines = Arrays.asList(getMessage().split("\n"));

        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("[ManaCore] " + ChatColor.RED + "################################################");
        sender.sendMessage("[ManaCore] " + ChatColor.RED + "##                                            ##");
        sender.sendMessage("[ManaCore] " + ChatColor.RED + "## An error occured while loading the plugin! ##");
        sender.sendMessage("[ManaCore] " + ChatColor.RED + "##                                            ##");
        sender.sendMessage("[ManaCore] " + ChatColor.RED + "################################################");
        sender.sendMessage("[ManaCore] ");
        sender.sendMessage("[ManaCore] " + ChatColor.RED + "Error:");
        messageLines.forEach(line -> sender.sendMessage("[ManaCore] " + ChatColor.RED + line));
        sender.sendMessage("[ManaCore] ");
        sender.sendMessage("[ManaCore] " + ChatColor.RED + "StackTrace:");

        int linesCounter = 0;

        for (String stackTraceLine : stackTrace.toString().split("\n")) {
            if (linesCounter > messageLines.size()) {
                if (!messageLines.contains(stackTraceLine)) {
                    sender.sendMessage("[ManaCore] " + ChatColor.RED + stackTraceLine);
                }
            } else {
                linesCounter++;
            }
        }

        sender.sendMessage("[ManaCore] ");
        sender.sendMessage("[ManaCore] " + ChatColor.RED + "################################################");
    }

    public enum ErrorLevel {

        SERVER_SHUTDOWN,
        CONTINUE

    }

}