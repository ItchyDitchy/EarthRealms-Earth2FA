package net.earthnetwork.earth2fa.lang;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.bgsoftware.common.config.CommentedConfiguration;

import net.earthnetwork.earth2fa.Earth2FAPlugin;
import net.earthnetwork.earth2fa.utility.debug.PluginDebugger;
import net.earthnetwork.earth2fa.utility.string.StringUtility;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public enum Message {

	ALREADY_REGISTERED,
    AUTHENTICATION,
    CONNECT_FAILURE,
    CONNECT_SUCCESS,
    CONNECT_USAGE,
    FIRST_AUTHENTICATION,
    HELP_GUIDE,
    NO_PERMISSION,
    NO_REGISTRATION,
    NOT_REGISTERED,
    REGISTER_USAGE,
    RELOAD_POST,
    RELOAD_PRE,
    RESET_USAGE,
    UNREGISTER_USAGE,
    UNREGISTER_ALERT,

    CUSTOM {
        @Override
        public void send(CommandSender sender, Locale locale, Object... objects) {
            String message = objects.length == 0 ? null : objects[0] == null ? null : objects[0].toString();
            boolean translateColors = objects.length >= 2 && objects[1] instanceof Boolean && (boolean) objects[1];
            if (message != null && !message.isEmpty())
                sender.sendMessage(translateColors ? StringUtility.translateColors(message) : message);
        }

    };

    private static final Earth2FAPlugin plugin = Earth2FAPlugin.getPlugin();

    private final String defaultMessage;
    private final Map<java.util.Locale, MessageContainer> messages = new HashMap<>();

    Message() {
        this(null);
    }

    Message(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public static void reload() {
    	Earth2FAPlugin.log("Loading messages started...");
        long startTime = System.currentTimeMillis();

        convertOldFile();
        PlayerLocales.clearLocales();

        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            plugin.saveResource("lang/en-US.yml", false);
        }

        int messagesAmount = 0;
        boolean countMessages = true;

        for (File langFile : Objects.requireNonNull(langFolder.listFiles())) {
            String fileName = langFile.getName().split("\\.")[0];
            java.util.Locale fileLocale;

            try {
                fileLocale = PlayerLocales.getLocale(fileName);
            } catch (IllegalArgumentException ex) {
            	Earth2FAPlugin.log("&cThe language \"" + fileName + "\" is invalid. Please correct the file name.");
                PluginDebugger.debug(ex);
                continue;
            }

            PlayerLocales.registerLocale(fileLocale);

            if ("en-US".equalsIgnoreCase(fileName))
                PlayerLocales.setDefaultLocale(fileLocale);

            CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(langFile);
            InputStream inputStream = plugin.getResource("lang/" + langFile.getName());

            try {
                cfg.syncWithConfig(langFile, inputStream == null ? plugin.getResource("lang/en-US.yml") : inputStream, "lang/en-US.yml");
            } catch (Exception ex) {
                PluginDebugger.debug(ex);
                ex.printStackTrace();
            }

            for (Message locale : values()) {
                if (cfg.isConfigurationSection(locale.name())) {
                    locale.setMessage(fileLocale, new ComplexMessage(locale.name(), cfg.getConfigurationSection(locale.name())));
                } else {
                    locale.setMessage(fileLocale, new RawMessage(locale.name(), StringUtility.translateColors(cfg.getString(locale.name(), ""))));
                }

                if (countMessages)
                    messagesAmount++;
            }

            countMessages = false;
        }

        Earth2FAPlugin.log(" - Found " + messagesAmount + " messages in the language files.");
        Earth2FAPlugin.log("Loading messages done (Took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    public boolean isEmpty(java.util.Locale locale) {
        MessageContainer messageContainer = messages.get(locale);
        return messageContainer == null || messageContainer.getMessage().isEmpty();
    }

    public String getMessage(java.util.Locale locale, Object... objects) {
        return isEmpty(locale) ? defaultMessage : replaceArgs(messages.get(locale).getMessage(), objects);
    }

    public void send(CommandSender sender, Object... objects) {
        send(sender, PlayerLocales.getLocale(sender), objects);
    }

    public void send(CommandSender sender, java.util.Locale locale, Object... objects) {
        MessageContainer messageContainer = messages.get(locale);
        if (messageContainer != null)
            messageContainer.sendMessage(sender, objects);
    }

    private void setMessage(java.util.Locale locale, MessageContainer messageContainer) {
        messages.put(locale, messageContainer);
    }

    private static String replaceArgs(String msg, Object... objects) {
        if (msg == null)
            return null;

        for (int i = 0; i < objects.length; i++) {
            String objectString = objects[i] instanceof BigDecimal ?
                    StringUtility.format((BigDecimal) objects[i]) : objects[i].toString();
            msg = msg.replace("{" + i + "}", objectString);
        }

        return msg;
    }

    private static void convertOldFile() {
        File file = new File(plugin.getDataFolder(), "lang.yml");
        if (file.exists()) {
            File dest = new File(plugin.getDataFolder(), "lang/en-US.yml");
            dest.getParentFile().mkdirs();
            file.renameTo(dest);
        }
    }

    private static abstract class MessageContainer {

        protected final String name;

        MessageContainer(String name) {
            this.name = name;
        }

        abstract String getMessage();

        abstract void sendMessage(CommandSender sender, Object... objects);

    }

    private static final class RawMessage extends MessageContainer {

        private final String message;

        RawMessage(String name, String message) {
            super(name);
            this.message = message;
        }

        @Override
        String getMessage() {
            return message;
        }

        @Override
        void sendMessage(CommandSender sender, Object... objects) {
            if (message != null && !message.isEmpty())
                sender.sendMessage(replaceArgs(message, objects));
        }
    }

    private static final class ComplexMessage extends MessageContainer {

        private final TextComponent[] textComponents;
        private final String rawMessage;
        private final String actionBarMessage;
        private final String titleMessage;
        private final String subtitleMessage;
        private final int fadeIn;
        private final int duration;
        private final int fadeOut;

        ComplexMessage(String name, ConfigurationSection section) {
            super(name);

            List<TextComponent> textComponents = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            String actionBarMessage = "";
            String titleMessage = null;
            String subtitleMessage = null;
            int fadeIn = -1;
            int fadeOut = -1;
            int duration = -1;

            for (String key : section.getKeys(false)) {
                if (key.equals("action-bar")) {
                    actionBarMessage = StringUtility.translateColors(section.getString(key + ".text"));
                } else if (key.equals("title")) {
                    titleMessage = StringUtility.translateColors(section.getString(key + ".title"));
                    subtitleMessage = StringUtility.translateColors(section.getString(key + ".sub-title"));
                    fadeIn = section.getInt(key + ".fade-in");
                    duration = section.getInt(key + ".duration");
                    fadeOut = section.getInt(key + ".fade-out");
                } else {
                    String message = StringUtility.translateColors(section.getString(key + ".text"));
                    stringBuilder.append(message);

                    TextComponent textComponent = new TextComponent(message);
                    textComponents.add(textComponent);

                    String toolTipMessage = section.getString(key + ".tooltip");
                    if (toolTipMessage != null) {
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new BaseComponent[]{new TextComponent(StringUtility.translateColors(toolTipMessage))}));
                    }

                    String commandMessage = section.getString(key + ".command");
                    if (commandMessage != null)
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandMessage));
                }
            }

            this.textComponents = textComponents.toArray(new TextComponent[0]);
            this.rawMessage = stringBuilder.toString();
            this.actionBarMessage = actionBarMessage;
            this.titleMessage = titleMessage;
            this.subtitleMessage = subtitleMessage;
            this.fadeIn = fadeIn;
            this.duration = duration;
            this.fadeOut = fadeOut;
        }

        private static BaseComponent[] replaceArgs(BaseComponent[] textComponents, Object... objects) {
            BaseComponent[] duplicate = new BaseComponent[textComponents.length];

            for (int i = 0; i < textComponents.length; i++) {
                duplicate[i] = textComponents[i].duplicate();
                if (duplicate[i] instanceof TextComponent) {
                    TextComponent textComponent = (TextComponent) duplicate[i];
                    textComponent.setText(Message.replaceArgs(textComponent.getText(), objects));
                }
                HoverEvent hoverEvent = duplicate[i].getHoverEvent();
                if (hoverEvent != null)
                    duplicate[i].setHoverEvent(new HoverEvent(hoverEvent.getAction(), replaceArgs(hoverEvent.getValue(), objects)));
            }

            return duplicate;
        }

        @Override
        public String getMessage() {
            return rawMessage;
        }

        @Override
        void sendMessage(CommandSender sender, Object... objects) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(rawMessage);
            } else {
                BaseComponent[] duplicate = replaceArgs(textComponents, objects);

                if (duplicate.length > 0)
                    ((Player) sender).spigot().sendMessage(duplicate);

                if (actionBarMessage != null)
                	((Player)sender).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.replaceArgs(actionBarMessage, objects)));

                ((Player)sender).sendTitle(Message.replaceArgs(titleMessage, objects),
                        Message.replaceArgs(subtitleMessage, objects), fadeIn, duration, fadeOut);
            }
        }

    }

}