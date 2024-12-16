package io.github.spigotrce.velocity.command.misc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import io.github.spigotrce.common.DiscordWebhookSender;
import io.github.spigotrce.common.Messages;
import io.github.spigotrce.velocity.VelocityEngine;
import io.github.spigotrce.velocity.command.VelocityCommand;
import io.github.spigotrce.velocity.database.ReportDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ReportCommand extends VelocityCommand {
    public static final HashMap<String, Long> lastUse = new HashMap<>();
    public static final Component incompleteCommand = Component.text("§5§lNexus§d§lCraft » §fIncomplete command. Usage: /report <player> <type>");
    public static final Component error = Component.text("§5§lNexus§d§lCraft » §4There was an error posting your report.");

    public ReportCommand() {
        super("report", "r");
    }

    @Override
    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder.<CommandSource>literal(commandName)
                .executes(commandContext -> {
                    commandContext.getSource().sendMessage(incompleteCommand);
                    return SINGLE_SUCCESS;
                })
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("user", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            String partialName;

                            try {
                                partialName = ctx.getArgument("user", String.class).toLowerCase();
                            } catch (IllegalArgumentException ignored) {
                                partialName = "";
                            }

                            if (partialName.isEmpty()) {
                                VelocityEngine.PROXY_SERVER.getAllPlayers().stream().map(Player::getUsername).forEach(builder::suggest);
                                return builder.buildFuture();
                            }

                            String finalPartialName = partialName;

                            VelocityEngine.PROXY_SERVER.getAllPlayers().stream().map(Player::getUsername).filter(name -> name.toLowerCase().startsWith(finalPartialName)).forEach(builder::suggest);

                            return builder.buildFuture();
                        }).executes(commandContext -> {
                            commandContext.getSource().sendMessage(incompleteCommand);
                            return SINGLE_SUCCESS;
                        })
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("type", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    String partialType;

                                    try {
                                        partialType = ctx.getArgument("type", String.class).toLowerCase();
                                    } catch (IllegalArgumentException ignored) {
                                        partialType = "";
                                    }

                                    if (partialType.isEmpty()) {
                                        VelocityEngine.CONFIG.getReportTypes().keySet().forEach(builder::suggest);
                                        return builder.buildFuture();
                                    }

                                    String finalPartialType = partialType;

                                    VelocityEngine.CONFIG.getReportTypes().keySet().stream().filter(name -> name.toLowerCase().startsWith(finalPartialType)).forEach(builder::suggest);

                                    return builder.buildFuture();
                                })
                                .executes(commandContext -> {
                                    if (!(commandContext.getSource() instanceof Player)) {
                                        commandContext.getSource().sendMessage(Messages.invalidSourcePlayer);
                                        return SINGLE_SUCCESS;
                                    }
                                    if (lastUse.get(((Player) commandContext.getSource()).getUsername()) != null) {
                                        if ((System.currentTimeMillis() - lastUse.get(((Player) commandContext.getSource()).getUsername())) < 180_000) {
                                            commandContext.getSource().sendMessage(Messages.commandUnderCooldown
                                                    .replaceText(TextReplacementConfig.builder()
                                                            .matchLiteral("{seconds}")
                                                            .replacement(String.valueOf((180_000 - (System.currentTimeMillis() - lastUse.get(((Player) commandContext.getSource())
                                                                    .getUsername()))) / 1000))
                                                            .build()
                                                    )
                                            );
                                            return SINGLE_SUCCESS;
                                        }
                                    }
                                    String playerName = commandContext.getArgument("user", String.class);
                                    String reportType = commandContext.getArgument("type", String.class);

                                    Player player = VelocityEngine.PROXY_SERVER.getPlayer(playerName).orElse(null);

                                    if (player == null) {
                                        commandContext.getSource().sendMessage(Messages.playerNotFound
                                                .replaceText(TextReplacementConfig.builder()
                                                        .matchLiteral("{user}")
                                                        .replacement(playerName)
                                                        .build()
                                                )
                                        );
                                        return SINGLE_SUCCESS;
                                    }

                                    if (Objects.equals(((Player) commandContext.getSource()).getUsername(), playerName)) {
                                        player.sendMessage(Messages.cannotDoSelf
                                                .replaceText(TextReplacementConfig.builder()
                                                        .matchLiteral("{action}")
                                                        .replacement("report")
                                                        .build()
                                                )
                                        );
                                        return SINGLE_SUCCESS;
                                    }

                                    if (!(VelocityEngine.CONFIG.getReportTypes().containsKey(reportType))) {
                                        commandContext.getSource().sendMessage(Messages.invalidReportType
                                                .replaceText(TextReplacementConfig.builder()
                                                        .matchLiteral("{type}")
                                                        .replacement(reportType)
                                                        .build()
                                                )
                                        );
                                        return SINGLE_SUCCESS;
                                    }

                                    try {

                                        ReportDatabase.ReportModel report = new ReportDatabase.ReportModel();
                                        report.reporter = ((Player) commandContext.getSource()).getUsername();
                                        report.accused = playerName;
                                        player.getCurrentServer().ifPresentOrElse(serverConnection -> report.serverName = serverConnection.getServerInfo().getName(), () -> report.serverName = "Unknown");
                                        report.reportType = reportType;
                                        report.reportID = UUID.nameUUIDFromBytes((
                                                ((Player) commandContext.getSource()).getUniqueId().toString() + " " +
                                                        player.getUniqueId() + " " +
                                                        System.currentTimeMillis() + " " +
                                                        reportType + " " +
                                                        report.serverName)
                                                .getBytes(StandardCharsets.UTF_8)).toString();

                                        VelocityEngine.REPORT_DATABASE.insertReport(report);

                                        String jsonPayload = String.format(
                                                "{"
                                                        + "\"embeds\": [{"
                                                        + "\"title\": \"Report Details\","
                                                        + "\"color\": 3447003,"
                                                        + "\"fields\": ["
                                                        + "{"
                                                        + "\"name\": \"**Report ID:**\","
                                                        + "\"value\": \"%s\","
                                                        + "\"inline\": false"
                                                        + "},"
                                                        + "{"
                                                        + "\"name\": \"**Reporter:**\","
                                                        + "\"value\": \"%s\","
                                                        + "\"inline\": false"
                                                        + "},"
                                                        + "{"
                                                        + "\"name\": \"**Accused:**\","
                                                        + "\"value\": \"%s\","
                                                        + "\"inline\": false"
                                                        + "},"
                                                        + "{"
                                                        + "\"name\": \"**Server Name:**\","
                                                        + "\"value\": \"%s\","
                                                        + "\"inline\": false"
                                                        + "},"
                                                        + "{"
                                                        + "\"name\": \"**Report Type:**\","
                                                        + "\"value\": \"%s\","
                                                        + "\"inline\": false"
                                                        + "}"
                                                        + "]"
                                                        + "}]"
                                                        + "}",
                                                report.reportID, report.reporter, report.accused, report.serverName, report.reportType
                                        );

                                        new DiscordWebhookSender(VelocityEngine.CONFIG.getReportWebhook()).sendMessage(jsonPayload);
                                    } catch (IOException | SQLException e) {
                                        commandContext.getSource().sendMessage(error);
                                        VelocityEngine.LOGGER.error("Error while posting report", e);
                                    }
                                    commandContext.getSource().sendMessage(Messages.playerReported
                                            .replaceText(TextReplacementConfig.builder()
                                                    .matchLiteral("{user}")
                                                    .replacement(playerName)
                                                    .build())
                                            .replaceText(TextReplacementConfig.builder()
                                                    .matchLiteral("{type}")
                                                    .replacement(reportType)
                                                    .build()
                                            )
                                    );
                                    lastUse.put(((Player) commandContext.getSource()).getUsername(), System.currentTimeMillis());
                                    return SINGLE_SUCCESS;
                                })
                        )).build();
        return new BrigadierCommand(command);
    }
}