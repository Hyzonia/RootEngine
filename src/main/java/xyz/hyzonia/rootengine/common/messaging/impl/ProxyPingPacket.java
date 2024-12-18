package xyz.hyzonia.rootengine.common.messaging.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import xyz.hyzonia.rootengine.common.messaging.Packet;

public class ProxyPingPacket extends Packet {
    private long syncID;

    public ProxyPingPacket() {
        super("proxy_ping");
    }

    public ProxyPingPacket(long syncID) {
        super("proxy_ping");
        this.syncID = syncID;
    }

    @Override
    public void encode() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeLong(syncID);
        buf = out.toByteArray();
    }

    @Override
    public void decode() {
        ByteArrayDataInput input = ByteStreams.newDataInput(buf);
        syncID = input.readLong();
    }

    public long getSyncID() {
        return syncID;
    }

    public void setSyncID(long syncID) {
        this.syncID = syncID;
    }
}
