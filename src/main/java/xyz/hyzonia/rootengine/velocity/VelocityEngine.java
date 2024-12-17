package xyz.hyzonia.rootengine.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import eu.kennytv.maintenance.api.MaintenanceProvider;
import eu.kennytv.maintenance.api.event.MaintenanceChangedEvent;
import eu.kennytv.maintenance.api.proxy.MaintenanceProxy;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import xyz.hyzonia.rootengine.common.messaging.MessagingConstants;
import xyz.hyzonia.rootengine.common.messaging.PacketFactory;
import xyz.hyzonia.rootengine.common.messaging.impl.CommandForwardPacket;
import xyz.hyzonia.rootengine.common.messaging.impl.HandshakePacket;
import xyz.hyzonia.rootengine.common.messaging.impl.HandshakeResponsePacket;
import xyz.hyzonia.rootengine.common.messaging.impl.NickUpdatePacket;
import xyz.hyzonia.rootengine.velocity.command.misc.NickCommand;
import xyz.hyzonia.rootengine.velocity.command.misc.ReportCommand;
import xyz.hyzonia.rootengine.velocity.command.staff.StaffChatCommand;
import xyz.hyzonia.rootengine.velocity.database.PlayerDatabase;
import xyz.hyzonia.rootengine.velocity.database.ReportDatabase;
import xyz.hyzonia.rootengine.velocity.database.VulcanDatabase;
import xyz.hyzonia.rootengine.velocity.listener.impl.*;
import xyz.hyzonia.rootengine.velocity.misc.BackendServer;
import xyz.hyzonia.rootengine.velocity.misc.NickManager;
import org.slf4j.Logger;
import xyz.hyzonia.rootengine.velocity.misc.ServerBalancer;
import xyz.hyzonia.rootengine.velocity.misc.StaffChat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Plugin(id = "rootengine",
        name = "RootEngine",
        version = "1.0",
        authors = "SpigotRCE",
        description = "A Minecraft network core.",
        dependencies = {
                @Dependency(id = "maintenance", optional = true)
        },
        url = "https://github.com/SpigotRCE/RootEngine"
)
public class VelocityEngine {
    public static VelocityEngine INSTANCE;

    public static PacketFactory PACKET_FACTORY;
    public static Config CONFIG;

    public static PlayerDatabase PLAYER_DATABASE;
    public static ReportDatabase REPORT_DATABASE;
    public static VulcanDatabase VULCAN_DATABASE;

    public static NickManager NICK_MANAGER;
    public static ServerBalancer SERVER_BALANCER;
    public static StaffChat STAFF_CHAT;

    public static Map<RegisteredServer, BackendServer> BACKEND_SERVERS;

    public static MaintenanceProxy MAINTENANCE_PROXY;

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
        try {
            CONFIG.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PLAYER_DATABASE = new PlayerDatabase(LOGGER);
        REPORT_DATABASE = new ReportDatabase(LOGGER);
        VULCAN_DATABASE = new VulcanDatabase(LOGGER);
        NICK_MANAGER = new NickManager();
        SERVER_BALANCER = new ServerBalancer();
        STAFF_CHAT = new StaffChat();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LOGGER.info("Starting VelocityEngine");
        checkCompatibility();
        registerListeners();
        registerCommands();
        initializePacketFactory();

        if (CONFIG.isMaintenanceHooked() && PROXY_SERVER.getPluginManager().isLoaded("maintenance")) {
            MAINTENANCE_PROXY = (MaintenanceProxy) MaintenanceProvider.get();
            MAINTENANCE_PROXY.getEventManager().registerListener(new MaintenanceListener(), MaintenanceChangedEvent.class);
            LOGGER.info("Hooked into maintenance proxy!");
        }
    }

    private void checkCompatibility() {
        LOGGER.info("Starting compatibility check...");

        if (!(PROXY_SERVER.getPluginManager().isLoaded("signedvelocity") || PROXY_SERVER.getPluginManager().isLoaded("unsignedvelocity"))) {
            LOGGER.warn("SignedVelocity plugin is not loaded! RootEngine may not work correctly!");
            LOGGER.warn("Please ensure that any of the plugins are installed on both proxy & server and is enabled.");
            LOGGER.warn("If you have signedVelocity installed and sure that this is a bug, contact us at https://github.com/Hyzonia");
        }

        if (PROXY_SERVER.getPluginManager().isLoaded("cleanstaffchat")) {
            LOGGER.warn("CleanStaffChat plugin is loaded! This may cause issues with RootEngine's staff chat.");
            LOGGER.warn("Please ensure that CleanStaffChat is disabled or removed.");
            LOGGER.warn("If you don't have CleanStaffChat installed and sure that this is a bug, contact us at https://github.com/Hyzonia");
        }

        if (PROXY_SERVER.getPluginManager().isLoaded("playerbalancer")) {
            LOGGER.warn("PlayerBalancer plugin is loaded! This may cause issues with RootEngine's server balancer.");
            LOGGER.warn("Please ensure that PlayerBalancer is disabled or removed.");
            LOGGER.warn("If you don't have PlayerBalancer installed and sure that this is a bug, contact us at https://github.com/Hyzonia");
        }

        LOGGER.info("Compatibility check completed! If you are seeing this message without any prior warnings, the plugin should work fine without any problems!");
    }

    private void registerListeners() {
        new PlayerConnectionListener(PROXY_SERVER, INSTANCE, LOGGER);
        new PlayerServerListener(PROXY_SERVER, INSTANCE, LOGGER);
        new StaffChatListener(PROXY_SERVER, INSTANCE, LOGGER);
        if (CONFIG.isVulcanSupportEnabled()) {
            PROXY_SERVER.getChannelRegistrar().register(MinecraftChannelIdentifier.from("vulcan:bungee"));
            new VulcanAlertListener(PROXY_SERVER, INSTANCE, LOGGER);
            LOGGER.info("Hooked in vulcan proxy!");
            if (!PROXY_SERVER.getPluginManager().isLoaded("vulcan")) {
                LOGGER.warn("Vulcan bungee/velocity plugin is not loaded, the hook will continue to work and store logs in the database but will not send global alerts!");
                LOGGER.warn("To get global alerts you must install the Vulcan bungee/velocity plugin!");
                LOGGER.warn("If you have Vulcan bungee/velocity installed and sure that this is a bug, contact us at https://github.com/Hyzonia");
            }
        }
    }

    private void registerCommands() {
        new ReportCommand();
        new NickCommand();
        new StaffChatCommand();
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
            if (!event.getIdentifier().equals(MinecraftChannelIdentifier.from(MessagingConstants.ENGINE_CHANNEL.channelName)))
                return;
            event.setResult(PluginMessageEvent.ForwardResult.handled());
            if (event.getSource() instanceof Player) return;

            PACKET_FACTORY.decodeAndApply(event.getData());
        });
    }
}
