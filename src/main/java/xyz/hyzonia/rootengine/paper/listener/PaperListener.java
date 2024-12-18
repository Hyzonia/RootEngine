package xyz.hyzonia.rootengine.paper.listener;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.hyzonia.rootengine.paper.PaperEngine;

import java.util.logging.Logger;

/**
 * This abstract class serves as a base for all listeners in the plugin.
 * It provides common functionality for registering listeners with the Bukkit server,
 * logging messages, and accessing essential plugin components.
 */
public abstract class PaperListener implements Listener {
    private final @NotNull String name;
    private final PaperEngine plugin;
    private final Server server;
    private final Logger logger;

    /**
     * Constructs a new Listener instance.
     *
     * @param plugin The main plugin instance.
     * @param server The Bukkit server instance.
     * @param logger The Logger instance for logging messages.
     */
    public PaperListener(PaperEngine plugin, Server server, Logger logger) {
        this.name = this.getClass().getSimpleName();
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;

        getServer().getPluginManager().registerEvents(this, getPlugin());
        getLogger().info("Registered listener " + getName());
    }

    /**
     * Returns the simple name of the Listener class.
     *
     * @return The name of the Listener.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Returns the main plugin instance.
     *
     * @return The plugin instance.
     */
    public PaperEngine getPlugin() {
        return plugin;
    }

    /**
     * Returns the Bukkit server instance.
     *
     * @return The Bukkit server instance.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Returns the Logger instance.
     *
     * @return The Logger instance.
     */
    public Logger getLogger() {
        return logger;
    }
}
