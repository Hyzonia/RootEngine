package xyz.hyzonia.rootengine.paper;

import dev.dejvokep.boostedyaml.route.Route;
import xyz.hyzonia.rootengine.common.config.ConfigProvider;

import java.nio.file.Path;

public class Config extends ConfigProvider {
    public Config(Path dataDirectory) {
        super("config_paper.yml", "file-version", dataDirectory.toFile());
    }

    public String getChatFormat() {
        return getFileConfig().getString("chat.format");
    }

    public boolean isChatFormatEnabled() {
        return getFileConfig().getBoolean(Route.fromString("chat.enabled"));
    }
}
