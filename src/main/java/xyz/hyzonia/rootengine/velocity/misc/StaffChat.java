package xyz.hyzonia.rootengine.velocity.misc;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import xyz.hyzonia.rootengine.common.DiscordWebhookSender;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;

import java.io.IOException;
import java.util.ArrayList;

public class StaffChat {
    public final ArrayList<Player> staffsWithChatEnabled; // if the staff can see and send staff messages
    public final ArrayList<Player> staffsWithChatToggled; // if all messages from the staff get sent to the staff chat
    
    public StaffChat() {
        staffsWithChatEnabled = new ArrayList<>();
        staffsWithChatToggled = new ArrayList<>();
    }

    public void broadcastStaffMessage(Component message) {
        staffsWithChatEnabled.forEach(player -> player.sendMessage(message));
        VelocityEngine.PROXY_SERVER.getConsoleCommandSource().sendMessage(message);
        try {
            new DiscordWebhookSender(VelocityEngine.CONFIG.getStaffChatWebhook()).sendMessage(message.toString());
        } catch (IOException e) {
            VelocityEngine.LOGGER.error("Error while posting to Discord staff chat", e);
        }
    }

    public void onUpdate() {
        staffsWithChatEnabled.forEach(player -> {
            if (!player.hasPermission("rootengine.staffchat"))
                staffsWithChatEnabled.remove(player);
        });

        staffsWithChatToggled.forEach(player -> {
            if (!player.hasPermission("rootengine.staffchat"))
                staffsWithChatToggled.remove(player);
        });
    }
}