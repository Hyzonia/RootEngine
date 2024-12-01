package io.github.spigotrce.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.spigotrce.common.messaging.MessagingConstants;
import io.github.spigotrce.common.messaging.PacketFactory;
import io.github.spigotrce.common.messaging.impl.CommandForwardPacket;
import io.github.spigotrce.common.messaging.impl.HandshakePacket;
import io.github.spigotrce.common.messaging.impl.HandshakeResponsePacket;
import io.github.spigotrce.common.messaging.impl.NickUpdatePacket;
import io.github.spigotrce.velocity.database.PlayerDatabase;
import io.github.spigotrce.velocity.database.ReportDatabase;
import io.github.spigotrce.velocity.listener.impl.PlayerConnectionListener;
import io.github.spigotrce.velocity.misc.BackendServer;
import io.github.spigotrce.velocity.misc.NickManager;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Map;

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
    public static ReportDatabase REPORT_DATABASE;

    public static NickManager NICK_MANAGER;

    public static Map<RegisteredServer, BackendServer> BACKEND_SERVERS;

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
        REPORT_DATABASE = new ReportDatabase(LOGGER);
        NICK_MANAGER = new NickManager();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LOGGER.info("Starting VelocityEngine");
        registerListeners();
        initializePacketFactory();
    }

    private void initializePacketFactory() {
        PACKET_FACTORY = new PacketFactory(sender -> ((ServerConnection) sender.connection).
                sendPluginMessage(MinecraftChannelIdentifier.from(MessagingConstants.ENGINE_CHANNEL.channelName), sender.data));

        PACKET_FACTORY.registerPacket("handshake", HandshakePacket::new, handshakePacket -> {
            // not s -> p
        });

        PACKET_FACTORY.registerPacket("handshake_response", HandshakeResponsePacket::new, handshakeResponsePacket -> {
            BACKEND_SERVERS.get(PROXY_SERVER.getServer(handshakeResponsePacket.getServerName()).get()).protocolVersion = handshakeResponsePacket.getServerPVN();
            LOGGER.debug("Backend server PVN: {}", BACKEND_SERVERS.get(PROXY_SERVER.getServer(handshakeResponsePacket.getServerName()).get()).protocolVersion);
        });

        PACKET_FACTORY.registerPacket("nick_update", NickUpdatePacket::new, nickUpdatePacket -> {
            // not s -> p
        });

        PACKET_FACTORY.registerPacket("command_forward", CommandForwardPacket::new, commandForwardPacket -> {
            PROXY_SERVER.getCommandManager().executeImmediatelyAsync(PROXY_SERVER.getPlayer(commandForwardPacket.getPlayerUUID()).get(), commandForwardPacket.getCommand());
        });

        PROXY_SERVER.getChannelRegistrar().register(MinecraftChannelIdentifier.from(MessagingConstants.ENGINE_CHANNEL.channelName));
        PROXY_SERVER.getEventManager().register(this, PluginMessageEvent.class, PostOrder.NORMAL, event -> {
            if (!event.getIdentifier().equals(MinecraftChannelIdentifier.from(MessagingConstants.ENGINE_CHANNEL.channelName))) return;
            event.setResult(PluginMessageEvent.ForwardResult.handled());
            if (event.getSource() instanceof Player) return;

            PACKET_FACTORY.decodeAndApply(event.getData());
        });
    }

    private void registerListeners() {
        new PlayerConnectionListener(PROXY_SERVER, INSTANCE, LOGGER);
    }
}
