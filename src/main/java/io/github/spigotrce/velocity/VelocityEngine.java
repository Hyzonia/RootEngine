package io.github.spigotrce.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.github.spigotrce.common.messaging.MessagingConstants;
import io.github.spigotrce.common.messaging.PacketFactory;
import io.github.spigotrce.velocity.database.PlayerDatabase;
import io.github.spigotrce.velocity.misc.NickManager;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "rootengine",
        name = "RootEngine",
        version = "1.0",
        authors = "SpigotRCE",
        description = "A Minecraft network core.",
        dependencies = {
        },
        url = "https://github.com/SpigotRCE/RootEngine"
)
public class VelocityEngine {
    public static VelocityEngine INSTANCE;

    public static PacketFactory PACKET_FACTORY;
    public static Config CONFIG;

    public static PlayerDatabase PLAYER_DATABASE;

    public static NickManager NICK_MANAGER;

    @Inject
    public static Logger LOGGER;
    @Inject
    @DataDirectory
    public static Path DATA_DIRECTORY;
    @Inject
    public static ProxyServer PROXY_SERVER;

    @Inject
    public VelocityEngine(Logger logger, @DataDirectory Path dataDirectory, ProxyServer proxyServer) {
        INSTANCE = this;
        LOGGER = logger;
        DATA_DIRECTORY = dataDirectory;
        PROXY_SERVER = proxyServer;
        CONFIG = new Config(DATA_DIRECTORY);
        PLAYER_DATABASE = new PlayerDatabase(LOGGER);
        NICK_MANAGER = new NickManager();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LOGGER.info("Starting VelocityEngine");
    }
}
