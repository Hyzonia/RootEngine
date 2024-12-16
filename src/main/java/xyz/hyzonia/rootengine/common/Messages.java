package xyz.hyzonia.rootengine.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class Messages {
    public static final String MISSING_PERMISSIONS = "§4§lYou do not have permission to use this command.";
    public static final String ERROR_READING_MESSAGE_NICK_UPDATE = "§4§lAn error occurred while reading nick update message.";
    public static final String DISPLAY_NAME_CHANGED = "§9Your nickname has been updated to §a{nickname}§r§9.";
    public static final Component socials_nexus = Component.text("\n §d📱 §lSOCIALS§r \n \n §fFollow us on social media to stay up to date! \n §d§nhttps://twitter.nexus-craft.org §r \n §d§nhttps://instagram.nexus-craft.org §r \n §d§nhttps://youtube.nexus-craft.org §r \n").decoration(TextDecoration.ITALIC, false);
    public static final Component socials_minecave = Component.text("\n §d📱 §lSOCIALS§r \n \n §fFollow us on social media to stay up to date! \n §d§nhttps://twitter.minecave.org §r \n §d§nhttps://instagram.minecave.org §r \n §d§nhttps://youtube.minecave.org §r \n").decoration(TextDecoration.ITALIC, false);
    public static final Component store_nexus = Component.text("\n §e🛒 §lSTORE§r \n \n §fPurchase ranks, coins, keys and much \n §fmore at our store § support the server! \n §e§nhttps://store.nexus-craft.org \n").decoration(TextDecoration.ITALIC, false);
    public static final Component store_minecave = Component.text("\n §e🛒 §lSTORE§r \n \n §fPurchase ranks, coins, keys and much \n §fmore at our store § support the server! \n §e§nhttps://store.minecave.org \n").decoration(TextDecoration.ITALIC, false);
    public static final Component discord_nexus = Component.text(" \n §b✉ §lDISCORD§r \n \n §fStay up to date with all announcements, \n §fgiveaways, events and much more! \n §fInteract with members in NexusCraft Community Discord! \n §b§nhttps://discord.nexus-craft.org \n").decoration(TextDecoration.ITALIC, false);
    public static final Component discord_minecave = Component.text(" \n §b✉ §lDISCORD§r \n \n §fStay up to date with all announcements, \n §fgiveaways, events and much more! \n §fInteract with members in MineCave Community Discord! \n §b§nhttps://discord.gg/minecave \n").decoration(TextDecoration.ITALIC, false);
    public static final Component help = Component.text("§aVisit §9§lhttps://help.nexus-craft.org§r §ato get information about the server.").decoration(TextDecoration.ITALIC, false);
    public static final Component serverKickMessage = Component.text(
            """
                    §m--------------------------------§r
                                                           \s
                     Kicked from: {server}§r
                     Reason: {reason}§r
                                                            §r
                    §m--------------------------------§r
                    """).decoration(TextDecoration.ITALIC, false);
    public static final Component ping = Component.text("§5§lNexus§d§lCraft » §7Your latency is: §d{ping} ms").decoration(TextDecoration.ITALIC, false);
    public static final Component pingOther = Component.text("§5§lNexus§d§lCraft » §d{user}§7's latency: {ping} ms").decoration(TextDecoration.ITALIC, false);
    public static final Component invalidSourceConsole = Component.text("§5§lNexus§d§lCraft » §4§lOnly players can execute this command!");
    public static final Component invalidSourcePlayer = Component.text("§5§lNexus§d§lCraft » §4§lOnly console can execute this command!");
    public static final Component cannotDoSelf = Component.text("§5§lNexus§d§lCraft » §4§lYou cannot {action} §4§lyourself!");
    public static final Component playerNotFound = Component.text("§5§lNexus§d§lCraft » §7The user §d{user} §7is not §donline§7.").decoration(TextDecoration.ITALIC, false);
    public static final Component playerReported = Component.text("§5§lNexus§d§lCraft » §7The user §d{user} §7has been reported successfully under type §d{type}§7.").decoration(TextDecoration.ITALIC, false);
    public static final Component invalidReportType = Component.text("§5§lNexus§d§lCraft » §7Report type §d{type} §7is not a valid report type.").decoration(TextDecoration.ITALIC, false);
    public static final Component commandUnderCooldown = Component.text("§5§lNexus§d§lCraft » §7The command is under cooldown for §d{seconds} §7second(s).").decoration(TextDecoration.ITALIC, false);
    public static final Component directMessage = Component.text("§5§lNexus§d§lCraft » §7[{sender} -> {receiver}] §d{message}§7.");
    public static final Component sendingToServer = Component.text("§5§lNexus§d§lCraft » §7Sending you to: §d{server}§7.").decoration(TextDecoration.ITALIC, false);
    public static final Component serverNotFound = Component.text("§5§lNexus§d§lCraft » §7Server: §d{server} §7not found.").decoration(TextDecoration.ITALIC, false);
    public static final Component incompleteCommand = Component.text("§5§lNexus§d§lCraft » §7Incomplete command.").decoration(TextDecoration.ITALIC, false);
    public static final Component databaseError = Component.text("§5§lNexus§d§lCraft » §7A database error has occurred, contact a server administrator if something doesn't work.").decoration(TextDecoration.ITALIC, false);
    public static final Component fileError = Component.text("§5§lNexus§d§lCraft » §7A file error has occurred, contact a server administrator if something doesn't work.").decoration(TextDecoration.ITALIC, false);
}
