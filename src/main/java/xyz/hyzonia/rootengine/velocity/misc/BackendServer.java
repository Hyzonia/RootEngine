package xyz.hyzonia.rootengine.velocity.misc;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.hyzonia.rootengine.common.messaging.impl.SyncPacket;

public class BackendServer {
    public RegisteredServer serverInfo;
    public int serverPVN;
    public int onlinePlayers;
    public int maxPlayers;
    public String motd;
    public String favicon;
    public long upTime;

    public BackendServer(RegisteredServer serverInfo, int serverPVN, int onlinePlayers, int maxPlayers, String motd, String favicon, long upTime) {
        this.serverInfo = serverInfo;
        this.serverPVN = serverPVN;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.motd = motd;
        this.favicon = favicon;
        this.upTime = upTime;
    }

    public void updateFromPacket(SyncPacket packet) {
        this.serverPVN = packet.getServerPVN();
        this.onlinePlayers = packet.getOnlinePlayers();
        this.maxPlayers = packet.getMaxPlayers();
        this.motd = packet.getMotd();
        this.favicon = packet.getFavicon();
        this.upTime = packet.getUpTime();
    }
}
