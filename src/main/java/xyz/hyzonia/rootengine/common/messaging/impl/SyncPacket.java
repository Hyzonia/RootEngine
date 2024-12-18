package xyz.hyzonia.rootengine.common.messaging.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import xyz.hyzonia.rootengine.common.messaging.Packet;

import java.util.ArrayList;
import java.util.UUID;

public class SyncPacket extends Packet {
    private String serverName;
    private int serverPVN;
    private int onlinePlayers;
    private int maxPlayers;
    private String motd;
    private String favicon;
    private long upTime;

    public SyncPacket() {
        super("sync");
    }

    public SyncPacket(String serverName, int serverPVN, int onlinePlayers, int maxPlayers, String motd, String favicon, long upTime) {
        super("sync");
        this.serverName = serverName;
        this.serverPVN = serverPVN;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.motd = motd;
        this.favicon = favicon;
        this.upTime = upTime;
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

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPVN() {
        return serverPVN;
    }

    public void setServerPVN(int serverPVN) {
        this.serverPVN = serverPVN;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }
}
