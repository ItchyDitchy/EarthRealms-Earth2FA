package net.earthrealms.manacore.module.handler;

import net.earthrealms.manacore.ManaCorePlugin;

public abstract class AbstractHandler {

    protected final ManaCorePlugin plugin;

    protected AbstractHandler(ManaCorePlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void loadData() throws HandlerLoadException;

}