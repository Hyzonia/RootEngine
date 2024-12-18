package xyz.hyzonia.rootengine.velocity.listener.impl;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import xyz.hyzonia.rootengine.common.Messages;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;
import xyz.hyzonia.rootengine.velocity.database.PlayerDatabase;
import xyz.hyzonia.rootengine.velocity.listener.VelocityListener;

import java.sql.SQLException;
import java.util.Objects;

public class PlayerConnectionListener extends VelocityListener {

    public PlayerConnectionListener(ProxyServer proxyServer, VelocityEngine plugin, Logger logger) {
        super(proxyServer, plugin, logger);
    }

    @Subscribe
    public void onPlayerConnect(PostLoginEvent event) {
        PlayerDatabase.PlayerModel playerModel = null;
        try {
            playerModel = VelocityEngine.PLAYER_DATABASE.getPlayer(event.getPlayer().getUniqueId().toString());
        } catch (Exception e) {
            getLogger().error("Failed to update player in database", e);
            event.getPlayer().sendMessage(Messages.databaseError);
        }

        if (Objects.isNull(playerModel)) {

            playerModel = new PlayerDatabase.PlayerModel();
            playerModel.playerID = event.getPlayer().getUniqueId().toString();
            playerModel.playerUsername = event.getPlayer().getUsername();
            playerModel.playerNickname = playerModel.playerUsername;
            playerModel.playerUsernameLower = playerModel.playerUsername.toLowerCase();
            if (event.getPlayer().getCurrentServer().isEmpty()) playerModel.serverName = "NONE";
            else playerModel.serverName = event.getPlayer().getCurrentServer().get().getServerInfo().getName();
            playerModel.playerFirstIP = event.getPlayer().getRemoteAddress().getHostName().toLowerCase();
            playerModel.playerLastIP = playerModel.playerFirstIP;
            playerModel.playerIsOnlineMode = event.getPlayer().isOnlineMode();
            try {
                VelocityEngine.PLAYER_DATABASE.insetPlayer(playerModel);
            } catch (SQLException e) {
                getLogger().error("Failed to insert player in database", e);
                event.getPlayer().sendMessage(Messages.databaseError);
            }
            return;
        }

        playerModel.playerLastIP = event.getPlayer().getRemoteAddress().getHostName().toLowerCase();
        playerModel.playerUsername = event.getPlayer().getUsername();
        playerModel.playerUsernameLower = playerModel.playerUsername.toLowerCase();
        PlayerDatabase.PlayerModel finalPlayerModel = playerModel;
        event.getPlayer().getCurrentServer().ifPresent(server -> finalPlayerModel.serverName = server.getServerInfo().getName());


        try {
            VelocityEngine.PLAYER_DATABASE.updatePlayer(finalPlayerModel);
        } catch (SQLException e) {
            getLogger().error("Failed to update player in database", e);
            event.getPlayer().sendMessage(Messages.databaseError);
        }

        try {
            VelocityEngine.NICK_MANAGER.setNick(event.getPlayer(), playerModel.playerNickname);
        } catch (SQLException e) {
            getLogger().error("Failed to update player in database", e);
            event.getPlayer().sendMessage(Messages.databaseError);
        }
    }
}
