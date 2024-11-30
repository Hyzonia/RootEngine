package io.github.spigotrce.common.messaging.impl;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.spigotrce.common.messaging.Packet;

import java.util.UUID;

public class NickUpdatePacket extends Packet {
    private UUID playerUUID;
    private String playerNickname;

    public NickUpdatePacket() {
        super("nick_update");
    }

    public NickUpdatePacket(UUID playerUUID, String playerNickname) {
        super("nick_update");
        this.playerUUID = playerUUID;
        this.playerNickname = playerNickname;
    }

    @Override
    public void encode() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(playerUUID.toString());
        out.writeUTF(playerNickname);
        buf = out.toByteArray();
    }

    @Override
    public void decode() {
        ByteArrayDataInput input = ByteStreams.newDataInput(buf);
        playerUUID = UUID.fromString(input.readUTF());
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
}
