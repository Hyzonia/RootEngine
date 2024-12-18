package xyz.hyzonia.rootengine.paper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.hyzonia.rootengine.common.Messages;
import xyz.hyzonia.rootengine.common.messaging.MessagingConstants;
import xyz.hyzonia.rootengine.common.messaging.PacketFactory;
import xyz.hyzonia.rootengine.common.messaging.impl.CommandForwardPacket;
import xyz.hyzonia.rootengine.common.messaging.impl.HandshakePacket;
import xyz.hyzonia.rootengine.common.messaging.impl.SyncPacket;
import xyz.hyzonia.rootengine.common.messaging.impl.NickUpdatePacket;
import xyz.hyzonia.rootengine.paper.listener.LPCListener;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Logger;

public class PaperEngine extends JavaPlugin {
    public static PaperEngine INSTANCE;
    public static Logger LOGGER;
    public static Path DATA_DIRECTORY;
    public static Config CONFIG;
    public static Server SERVER;
    public static PacketFactory PACKET_FACTORY;
    public static HashMap<Player, Integer> SYNC_THREADS;
    public static HashMap<Player, Integer> SYNC_IDS;

    public PaperEngine() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        DATA_DIRECTORY = getDataFolder().toPath();
        CONFIG = new Config(getDataFolder().toPath());
        SERVER = getServer();
        SYNC_THREADS = new HashMap<>();

        LOGGER.info("Starting PaperEngine");

        initializePacketFactory();

        new LPCListener(this, getServer(), getLogger());
    }

    @Override
    public void onDisable() {
        LOGGER.info("Stopping PaperEngine");
        SYNC_THREADS.values().forEach(id -> getServer().getScheduler().cancelTask(id));
        SYNC_THREADS.clear();
        SYNC_IDS.clear();
    }

    private void initializePacketFactory() {
        PACKET_FACTORY = new PacketFactory(sender -> ((Player) sender.connection).sendPluginMessage(this, MessagingConstants.ENGINE_CHANNEL.channelName, sender.data));

        PACKET_FACTORY.registerPacket("handshake", HandshakePacket::new, handshakePacket -> {
            Player player = getServer().getPlayer(handshakePacket.getPlayerUUID());
            player.setDisplayName(handshakePacket.getPlayerNickname());
            player.sendMessage(Messages.DISPLAY_NAME_CHANGED.replace("{nickname}", handshakePacket.getPlayerNickname()));

            SYNC_IDS.put(player, 0);
            PACKET_FACTORY.encodeAndSend(
                    new SyncPacket(
                            handshakePacket.getServerName(),
                            getServer().getUnsafe().getProtocolVersion(),
                            getServer().getOnlinePlayers().size(),
                            getServer().getMaxPlayers(),
                            getServer().getMotd(),
                            getServer().getServerIcon().getData(),
                            System.currentTimeMillis(),
                            SYNC_IDS.get(player)
                    ),
                    player
            );

            SYNC_THREADS.put(player,
                    getServer().getScheduler().scheduleSyncRepeatingTask(
                            this,
                            () -> {
                                SYNC_IDS.put(player, SYNC_IDS.get(player) + 1);
                                PACKET_FACTORY.encodeAndSend(
                                        new SyncPacket(
                                                handshakePacket.getServerName(),
                                                getServer().getUnsafe().getProtocolVersion(),
                                                getServer().getOnlinePlayers().size(),
                                                getServer().getMaxPlayers(),
                                                getServer().getMotd(),
                                                getServer().getServerIcon().getData(),
                                                System.currentTimeMillis(),
                                                SYNC_IDS.get(player)
                                        ),
                                        player
                                );
                            },
                            20L,
                            20L
                    )
            );


            getServer().broadcast(
                    Component.text("§b§l[+] {nickname}")
                            .replaceText(
                                    TextReplacementConfig.builder()
                                            .matchLiteral("{nickname}")
                                            .replacement(player.displayName().color(TextColor.fromHexString("#55FFFF")))
                                            .build()
                            )
            );
        });

        PACKET_FACTORY.registerPacket("sync", SyncPacket::new, syncPacket -> {
            // not p -> s
        });

        PACKET_FACTORY.registerPacket("nick_update", NickUpdatePacket::new, nickUpdatePacket -> {
            try {
                getServer().getPlayer(nickUpdatePacket.getPlayerUUID()).setDisplayName(nickUpdatePacket.getPlayerNickname());
                getServer().getPlayer(nickUpdatePacket.getPlayerUUID()).sendMessage(Messages.DISPLAY_NAME_CHANGED.replace("{nickname}", nickUpdatePacket.getPlayerNickname()));
            } catch (Exception e) {
                getServer().getPlayer(nickUpdatePacket.getPlayerUUID()).sendMessage(Messages.ERROR_READING_MESSAGE_NICK_UPDATE);
            }
        });

        PACKET_FACTORY.registerPacket("command_forward", CommandForwardPacket::new, commandForwardPacket -> {
            Bukkit.dispatchCommand(getServer().getPlayer(commandForwardPacket.getPlayerUUID()), commandForwardPacket.getCommand());
        });

        SERVER.getMessenger().registerIncomingPluginChannel(this, MessagingConstants.ENGINE_CHANNEL.channelName, (String channel, Player player, byte[] data) -> {
            if (!channel.equals(MessagingConstants.ENGINE_CHANNEL.channelName)) return;
            if (data == null) return;

            PACKET_FACTORY.decodeAndApply(data);
        });
    }
}
