package xyz.hyzonia.rootengine.paper.command;

import org.bukkit.Server;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import xyz.hyzonia.rootengine.paper.PaperEngine;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

public abstract class PaperCommand implements CommandExecutor, TabCompleter {
    private final String name;
    private final String[] aliases;
    private final PaperEngine plugin;
    private final Server server;
    private final Logger logger;

    public PaperCommand(PaperEngine plugin, Server server, Logger logger, String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;

        org.bukkit.command.Command command = new _Command(getName());
        command.setAliases(List.of(this.aliases));

        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(getServer());
            commandMap.register("rootengine", command);
            getLogger().info("Registered command " + getName());
        } catch (Exception e) {
            getLogger().severe("Error creating command " + command.getName());
            getLogger().severe(e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public PaperEngine getPlugin() {
        return plugin;
    }

    public Server getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    private class _Command extends Command {
        public _Command(String name) {
            super(name);
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            return PaperCommand.this.onCommand(sender, this, commandLabel, args);
        }
    }
}
