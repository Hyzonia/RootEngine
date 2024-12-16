package xyz.hyzonia.rootengine.paper.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.hyzonia.rootengine.paper.PaperEngine;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LPCListener extends PaperListener {
    private final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private final LuckPerms luckPermsAPI;

    public LPCListener(PaperEngine plugin, Server server, Logger logger) {
        super(plugin, server, logger);
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPermsAPI = provider.getProvider();
        } else {
            this.luckPermsAPI = null;
            logger.severe("LuckPerms not found, LuckPerms Chat formatting will not work.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (luckPermsAPI == null) return;
        final String message = event.getMessage();
        final Player player = event.getPlayer();

        final CachedMetaData metaData = this.luckPermsAPI.getPlayerAdapter(Player.class).getMetaData(player);


        String format = PaperEngine.CONFIG.getChatFormat()
                .replace("{prefix}", metaData.getPrefix() != null ? metaData.getPrefix() : "")
                .replace("{suffix}", metaData.getSuffix() != null ? metaData.getSuffix() : "")
                .replace("{prefixes}", metaData.getPrefixes().keySet().stream().map(key -> metaData.getPrefixes().get(key)).collect(Collectors.joining()))
                .replace("{suffixes}", metaData.getSuffixes().keySet().stream().map(key -> metaData.getSuffixes().get(key)).collect(Collectors.joining()))
                .replace("{world}", player.getWorld().getName())
                .replace("{name}", player.getName())
                .replace("{displayname}", player.getDisplayName())
                .replace("{username-color}", metaData.getMetaValue("username-color") != null ? metaData.getMetaValue("username-color") : "")
                .replace("{message-color}", metaData.getMetaValue("message-color") != null ? metaData.getMetaValue("message-color") : "");

        format = colorize(translateHexColorCodes(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(player, format) : format));

        event.setFormat(format.replace("{message}", player.hasPermission("nexus.chat.colorcodes") && player.hasPermission("nexus.chat.rgbcodes")
                ? colorize(translateHexColorCodes(message)) : player.hasPermission("nexus.chat.colorcodes") ? colorize(message) : player.hasPermission("nexus.chat.rgbcodes")
                ? translateHexColorCodes(message) : message).replace("%", "%%"));
    }

    private String colorize(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String translateHexColorCodes(final String message) {
        final char colorChar = ChatColor.COLOR_CHAR;

        final Matcher matcher = HEX_PATTERN.matcher(message);
        final StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }
}
