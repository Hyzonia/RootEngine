package io.github.spigotrce.velocity.command;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import io.github.spigotrce.velocity.VelocityEngine;

/**
 * This abstract class serves as a base for creating custom commands in a Velocity plugin.
 * It automatically registers the command with the proxy server using Brigadier and provides
 * common functionality for executing and tab-completing commands.
 */
public abstract class VelocityCommand {
    public final String commandName;
    public final String[] commandAliases;
    public final int SINGLE_SUCCESS = Command.SINGLE_SUCCESS;

    /**
     * Constructs a new Command instance and registers the command with the server's CommandMap.
     *
     * @param commandName     The name of the command.
     * @param commandAliases The command aliases.
     */
    public VelocityCommand(String commandName, String... commandAliases) {
        this.commandName = commandName;
        this.commandAliases = commandAliases;

        VelocityEngine.PROXY_SERVER.getCommandManager().register(
                VelocityEngine.PROXY_SERVER.getCommandManager().metaBuilder(commandName)
                        .aliases(commandAliases).plugin(VelocityEngine.INSTANCE).build(),
                this.build()
        );
        VelocityEngine.LOGGER.info("Registered command {}", commandName);
    }

    /**
     * Returns the BrigadierCommand implementation for this command.
     *
     * @return The BrigadierCommand implementation.
     */
    public abstract BrigadierCommand build();
}