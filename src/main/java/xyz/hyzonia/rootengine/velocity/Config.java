package xyz.hyzonia.rootengine.velocity;

import dev.dejvokep.boostedyaml.route.Route;
import xyz.hyzonia.rootengine.common.config.ConfigProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config extends ConfigProvider {
    public Config(Path dataDirectory) {
        super("config_velocity.yml", "file-version", dataDirectory.toFile());
    }

    public String getDatabaseHost() {
        return getFileConfig().getString(Route.fromString("database.host"));
    }

    public int getDatabasePort() {
        return getFileConfig().getInt(Route.fromString("database.port"));
    }

    public String getDatabaseUsername() {
        return getFileConfig().getString(Route.fromString("database.username"));
    }

    public String getDatabaseName() {
        return getFileConfig().getString(Route.fromString("database.name"));
    }

    public String getDatabasePassword() {
        return getFileConfig().getString(Route.fromString("database.password"));
    }

    public Map<String, Map<String, String>> getReportTypes() {
        Map<String, Map<String, String>> types = new HashMap<>();
        getFileConfig().getSection(Route.fromString("report.types")).getKeys().forEach(type -> {
            Map<String, String> fields = new HashMap<>();
            getFileConfig().getSection(Route.fromString("report.types." + type)).getKeys().forEach(field -> fields.put((String) field, getFileConfig().getString(Route.fromString("report.types." + type + "." + field))));
            types.put((String) type, fields);
        });

        return types;
    }

    public Map<String, List<String>> getBalancer() {
        Map<String, List<String>> balancer = new HashMap<>();

        getFileConfig().getSection(Route.fromString("balancer")).getKeys().forEach(group -> {
            List<String> servers = getFileConfig().getStringList(Route.fromString("balancer." + group));
            balancer.put((String) group, servers);
        });

        return balancer;
    }

    public String getReportWebhook() {
        return getFileConfig().getString(Route.fromString("report.webhook"));
    }

    public List<String> getSelfSendServers() {
        return getFileConfig().getStringList(Route.fromString("self-send.servers"));
    }

    public boolean isMaintenanceHooked() {
        return getFileConfig().getBoolean(Route.fromString("maintenance-support.enabled"));
    }

    public String getMaintenanceWebhook() {
        return getFileConfig().getString(Route.fromString("maintenance-support.webhook"));
    }

    public String getMaintenanceMessage() {
        return getFileConfig().getString(Route.fromString("maintenance-support.discord-message"));
    }
}
