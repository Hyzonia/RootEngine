package xyz.hyzonia.rootengine.velocity.listener.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import org.slf4j.Logger;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;
import xyz.hyzonia.rootengine.velocity.database.VulcanDatabase;
import xyz.hyzonia.rootengine.velocity.listener.VelocityListener;

import java.sql.SQLException;

public class VulcanAlertListener extends VelocityListener {

    public VulcanAlertListener(ProxyServer proxyServer, VelocityEngine plugin, Logger logger) {
        super(proxyServer, plugin, logger);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!VelocityEngine.CONFIG.isVulcanSupportEnabled()) return;
        if (!event.getIdentifier().getId().equals("vulcan:bungee")) return;
        event.setResult(PluginMessageEvent.ForwardResult.handled());
        if (!(event.getSource() instanceof ServerConnection serverConnection)) return;
        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        switch (input.readUTF()) {
            case "alert": {
                handleAlert(serverConnection, input);
                break;
            }
        }
    }

    private void handleAlert(ServerConnection serverConnection, ByteArrayDataInput input) {
        String[] components = input.readUTF().split("#VULCAN#");
        String checkName = components[0];
        String checkType = components[1];
        String vl = components[2];
        String player = components[3];
        String maxVl = components[4];
        String clientVersion = components[5];
        String tps = components[6];
        String ping = components[7];
        String description = components[8];
        String info = components[9];
        String dev = components[10];
        String severity = components[11];
        String serverName = serverConnection.getServer().getServerInfo().getName();
        try {
            VelocityEngine.VULCAN_DATABASE.insertVulcanAlert(new VulcanDatabase.VulcanAlert(checkName, checkType, Integer.parseInt(vl), player, Integer.parseInt(maxVl), serverName, clientVersion, Float.parseFloat(tps), Integer.parseInt(ping), description, info, dev, severity, null));
        } catch (SQLException e) {
            VelocityEngine.LOGGER.error("Failed to insert alert into database", e);
        }
    }
}
