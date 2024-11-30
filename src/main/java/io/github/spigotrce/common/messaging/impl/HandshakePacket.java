package io.github.spigotrce.common.messaging.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.spigotrce.common.messaging.Packet;

import java.util.UUID;

public class HandshakePacket extends Packet {
    private UUID playerUUID;
    private String serverName;
    private String playerNickname;

    public HandshakePacket() {
        super("handshake");
    }

    public HandshakePacket(UUID playerUUID, String serverName, String playerNickname) {
        super("handshake");
        this.playerUUID = playerUUID;
        this.serverName = serverName;
        this.playerNickname = playerNickname;
    }

    @Override
    public void encode() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(playerUUID.toString());
        out.writeUTF(serverName);
        out.writeUTF(playerNickname);
        buf = out.toByteArray();
    }

    @Override
    public void decode() {
        ByteArrayDataInput input = ByteStreams.newDataInput(buf);
        playerUUID = UUID.fromString(input.readUTF());
        serverName = input.readUTF();
        playerNickname = input.readUTF();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
