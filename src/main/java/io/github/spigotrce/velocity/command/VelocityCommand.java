package io.github.spigotrce.velocity.command;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.spigotrce.velocity.VelocityEngine;
import org.slf4j.Logger;

/**
 * This abstract class serves as a base for creating custom commands in a Velocity plugin.
 * It automatically registers the command with the proxy server using Brigadier and provides
 * common functionality for executing and tab-completing commands.
 */
public abstract class VelocityCommand {
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final VelocityEngine plugin;
    private final String name;
    private final String[] aliases;
    private final int SINGLE_SUCCESS;

    /**
     * Constructs a new Command instance and registers the command with the server's CommandMap.
     *
     * @param name     The name of the command.
     * @param aliases The command aliases.
     */
    public VelocityCommand(ProxyServer proxyServer, Logger logger, VelocityEngine plugin, String name, String... aliases) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.plugin = plugin;
        this.name = name;
        this.aliases = aliases;
        this.SINGLE_SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;

        proxyServer.getCommandManager().register(proxyServer.getCommandManager().metaBuilder(getName()).aliases(getAliases()).plugin(plugin).build(), this.build());

        getLogger().info("Registered command {}", getName());
    }

    /**
     * Returns the BrigadierCommand implementation for this command.
     *
     * @return The BrigadierCommand implementation.
     */
    public abstract BrigadierCommand build();

    /**
     * Returns the simple name of the Command class.
     *
     * @return The name of the Command.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the aliases for the command.
     *
     * @return The aliases for the command.
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Returns the main plugin instance.
     *
     * @return The plugin instance.
     */
    public VelocityEngine getPlugin() {
        return plugin;
    }

    /**
     * Returns the Velocity proxy server instance.
     *
     * @return The Velocity proxy server instance.
     */
    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    /**
     * Returns the Logger instance.
     *
     * @return The Logger instance.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns SINGLE_SUCCESS value for denoting the execution as success.
     *
     * @return SINGLE_SUCCESS value for denoting the execution as success.
     */
    public int getSingleSuccess() {
        return SINGLE_SUCCESS;
    }
}