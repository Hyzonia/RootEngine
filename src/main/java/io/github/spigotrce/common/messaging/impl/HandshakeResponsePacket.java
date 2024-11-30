package io.github.spigotrce.common.messaging.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.spigotrce.common.messaging.Packet;

public class HandshakeResponsePacket extends Packet {
    private String serverName;
    private int serverPVN;

    public HandshakeResponsePacket() {
        super("handshake_response");
    }

    public HandshakeResponsePacket(int serverPVN, String serverName) {
        super("handshake_response");
        this.serverName = serverName;
        this.serverPVN = serverPVN;
    }

    @Override
    public void encode() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(serverName);
        out.writeInt(serverPVN);
        buf = out.toByteArray();
    }

    @Override
    public void decode() {
        ByteArrayDataInput input = ByteStreams.newDataInput(buf);
        serverName = input.readUTF();
        serverPVN = input.readInt();
    }

    public int getServerPVN() {
        return serverPVN;
    }

    public void setServerPVN(int serverPVN) {
        this.serverPVN = serverPVN;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
