package xyz.hyzonia.rootengine.velocity.misc;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ServerBalancer {
    private final Map<String, List<String>> balancer;

    public ServerBalancer() {
        this.balancer = VelocityEngine.CONFIG.getBalancer();
        VelocityEngine.LOGGER.info("Balancer -> {}", this.balancer.toString());
    }

    public boolean onRequest(Player player, String serverName) {
        if (balancer.get(serverName) == null) return false;

        List<String> servers = balancer.get(serverName);
        AtomicReference<RegisteredServer> leastPlayerServer = new AtomicReference<>();
        AtomicInteger i = new AtomicInteger(Integer.MAX_VALUE);
        servers.forEach(server -> {
            RegisteredServer s = VelocityEngine.PROXY_SERVER.getServer(server).orElse(null);
            if (s != null) {
                if (s.getPlayersConnected().size() < i.get()) {
                    leastPlayerServer.set(s);
                    i.set(s.getPlayersConnected().size());
                }
            } else
                VelocityEngine.LOGGER.warn("Server {} is not registered", server);
        });
        player.createConnectionRequest(leastPlayerServer.get()).fireAndForget();

        return true;
    }
}
