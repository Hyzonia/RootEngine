package io.github.spigotrce.velocity.misc;

import com.velocitypowered.api.proxy.server.RegisteredServer;

public class BackendServer {
    public RegisteredServer serverInfo;
    public int protocolVersion;

    public BackendServer(RegisteredServer serverInfo, int protocolVersion) {
        this.serverInfo = serverInfo;
        this.protocolVersion = protocolVersion;
    }
}
