package io.github.spigotrce.paper;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.logging.Logger;

public class PaperEngine extends JavaPlugin {
    public static PaperEngine INSTANCE;
    public static Logger LOGGER;
    public static Path DATA_DIRECTORY;
    public static Server SERVER;

    public PaperEngine() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        DATA_DIRECTORY = getDataFolder().toPath();
        SERVER = getServer();

        LOGGER.info("Starting PaperEngine");
    }

    @Override
    public void onDisable() {
        LOGGER.info("Stopping PaperEngine");
    }
}
