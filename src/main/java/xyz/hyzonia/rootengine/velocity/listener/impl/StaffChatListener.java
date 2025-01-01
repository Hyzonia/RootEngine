package xyz.hyzonia.rootengine.velocity.listener.impl;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;
import xyz.hyzonia.rootengine.velocity.listener.VelocityListener;

public class StaffChatListener extends VelocityListener {
    public StaffChatListener(ProxyServer proxyServer, VelocityEngine plugin, Logger logger) {
        super(proxyServer, plugin, logger);
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        if (!VelocityEngine.CONFIG.isStaffChatEnabled()) return;
        if (event.getPlayer().hasPermission("rootengine.staffchat") && VelocityEngine.STAFF_CHAT.staffsWithChatToggled.contains(event.getPlayer())) {
            VelocityEngine.STAFF_CHAT.broadcastStaffMessage(Component.text(event.getPlayer().getUsername()), Component.text(event.getMessage()));
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }
}
