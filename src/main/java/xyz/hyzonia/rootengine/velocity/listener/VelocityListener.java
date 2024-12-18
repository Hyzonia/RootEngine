package xyz.hyzonia.rootengine.velocity.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;

public abstract class VelocityListener {
    private final String name;
    private final ProxyServer proxyServer;
    private final VelocityEngine plugin;
    private final Logger logger;

    public VelocityListener(ProxyServer proxyServer, VelocityEngine plugin, Logger logger) {
        this.name = this.getClass().getSimpleName();
        this.proxyServer = proxyServer;
        this.plugin = plugin;
        this.logger = logger;

        getProxyServer().getEventManager().register(getPlugin(), this);
        getLogger().info("Registered listener {}", getName());
    }

    public String getName() {
        return name;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public VelocityEngine getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }
}
