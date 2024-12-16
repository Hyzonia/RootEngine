package xyz.hyzonia.rootengine.common.messaging.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import xyz.hyzonia.rootengine.common.messaging.Packet;

import java.util.UUID;

public class CommandForwardPacket extends Packet {
    private UUID playerUUID;
    private String command;

    public CommandForwardPacket() {
        super("command_forward");
    }

    public CommandForwardPacket(UUID playerUUID, String command) {
        super("command_forward");
        this.playerUUID = playerUUID;
        this.command = command;
    }

    @Override
    public void encode() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(playerUUID.toString());
        out.writeUTF(command);
        buf = out.toByteArray();
    }

    @Override
    public void decode() {
        ByteArrayDataInput input = ByteStreams.newDataInput(buf);
        playerUUID = UUID.fromString(input.readUTF());
        command = input.readUTF();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
