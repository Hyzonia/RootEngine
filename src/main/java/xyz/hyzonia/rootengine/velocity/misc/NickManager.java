package xyz.hyzonia.rootengine.velocity.misc;

import com.velocitypowered.api.proxy.Player;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;
import xyz.hyzonia.rootengine.velocity.database.PlayerDatabase;

import java.sql.SQLException;
import java.util.HashMap;

public class NickManager {
    private final HashMap<Player, String> nicks;

    public NickManager() {
        this.nicks = new HashMap<>();
    }

    public void setNick(Player player, String nick) throws SQLException {
        PlayerDatabase.PlayerModel playerModel = VelocityEngine.PLAYER_DATABASE.getPlayer(player.getUniqueId().toString());
        playerModel.playerNickname = nick;
        VelocityEngine.PLAYER_DATABASE.updatePlayer(playerModel);
        nicks.put(player, nick);
    }

    public String getNick(Player player) {
        return nicks.getOrDefault(player, player.getUsername());
    }

    public void resetNick(Player player) throws SQLException {
        setNick(player, player.getUsername());
    }
}
