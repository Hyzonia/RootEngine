package xyz.hyzonia.rootengine.velocity.command.staff;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import xyz.hyzonia.rootengine.common.Messages;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;
import xyz.hyzonia.rootengine.velocity.command.VelocityCommand;

public class StaffChatCommand extends VelocityCommand {
    public StaffChatCommand() {
        super("staffchat", "sc");
    }

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder.<CommandSource>literal(super.commandName)
                .requires(source ->
                        source.getPermissionValue("rootengine.staffchat") == Tristate.TRUE)
                .executes(commandContext -> {
                    if (!(commandContext.getSource() instanceof Player player)) {
                        commandContext.getSource().sendMessage(Messages.invalidSourceConsole);
                        return SINGLE_SUCCESS;
                    }

                    if (!VelocityEngine.STAFF_CHAT.staffsWithChatToggled.contains(player))
                        VelocityEngine.STAFF_CHAT.staffsWithChatToggled.add(player);
                    else
                        VelocityEngine.STAFF_CHAT.staffsWithChatToggled.remove(player);

                    return SINGLE_SUCCESS;
                })
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("message", StringArgumentType.word()).executes(commandContext -> {
                    VelocityEngine.STAFF_CHAT.broadcastStaffMessage(
                            commandContext.getSource() instanceof Player player ?
                                    Component.text(player.getUsername()) : Messages.consoleUsername,
                            Component.text(commandContext.getArgument("message", String.class)
                            )
                    );
                    return SINGLE_SUCCESS;

                })).build();

        return new BrigadierCommand(command);
    }
}
