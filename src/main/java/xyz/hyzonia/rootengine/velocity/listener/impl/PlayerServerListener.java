package xyz.hyzonia.rootengine.velocity.listener.impl;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.slf4j.Logger;
import xyz.hyzonia.rootengine.common.Messages;
import xyz.hyzonia.rootengine.common.messaging.impl.HandshakePacket;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;
import xyz.hyzonia.rootengine.velocity.database.PlayerDatabase;
import xyz.hyzonia.rootengine.velocity.listener.VelocityListener;

import java.sql.SQLException;

public class PlayerServerListener extends VelocityListener {

    public PlayerServerListener(ProxyServer proxyServer, VelocityEngine plugin, Logger logger) {
        super(proxyServer, plugin, logger);
    }

    @Subscribe
    public void onPlayerDisconnect(KickedFromServerEvent event) {
//        event.setResult(KickedFromServerEvent.Notify.create(Component.text("Kicked From Server")));
//        event.setResult(KickedFromServerEvent.RedirectPlayer.create(this.proxy.getServer("hub").orElse("hub")));
        if (event.getServerKickReason().isPresent()) {
            Component reason = event.getServerKickReason().orElse(Component.text("Unknown"));
            event.setResult(KickedFromServerEvent.Notify.create(Messages.serverKickMessage
                            .replaceText(TextReplacementConfig.builder()
                                    .matchLiteral("{server}")
                                    .replacement(event.getServer().getServerInfo().getName())
                                    .build())
                            .replaceText(TextReplacementConfig.builder()
                                    .matchLiteral("{reason}")
                                    .replacement(reason)
                                    .build())
                    )
            );
        }
    }

    @Subscribe
    public void onConnectionRequest(ServerPreConnectEvent event) {
        if (VelocityEngine.SERVER_BALANCER.onRequest(event.getPlayer(), event.getOriginalServer().getServerInfo().getName()))
            event.setResult(ServerPreConnectEvent.ServerResult.denied());
        else
            event.getPlayer().sendMessage(Messages.sendingToServer
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("{server}")
                            .replacement(event.getOriginalServer().getServerInfo().getName())
                            .build()
                    )
            );
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        PlayerDatabase.PlayerModel playerModel = null;
        try {
            playerModel = VelocityEngine.PLAYER_DATABASE.getPlayer(event.getPlayer().getUniqueId().toString());
        } catch (Exception e) {
            event.getPlayer().sendMessage(Component.text("Unable to load your information form the database"));
            return;
        }

        playerModel.serverName = event.getServer().getServerInfo().getName();

        try {
            VelocityEngine.PLAYER_DATABASE.updatePlayer(playerModel);
        } catch (SQLException e) {
            getLogger().error("Failed to update player in database", e);
            event.getPlayer().sendMessage(Messages.databaseError);
        }
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        event.getPlayer().getCurrentServer().ifPresent(server ->
                VelocityEngine.PACKET_FACTORY.encodeAndSend(
                        new HandshakePacket(
                                event.getPlayer().getUniqueId(),
                                server.getServerInfo().getName(),
                                getPlugin().NICK_MANAGER.getNick(event.getPlayer())
                        ),
                        server
                )
        );
    }
}
