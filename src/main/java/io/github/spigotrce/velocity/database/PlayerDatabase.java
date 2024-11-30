package io.github.spigotrce.velocity.database;

import io.github.spigotrce.velocity.VelocityEngine;
import org.slf4j.Logger;

import java.sql.*;

public class PlayerDatabase {
    private final Logger logger;
    private Connection connection;

    public PlayerDatabase(Logger logger) {
        this.logger = logger;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            this.logger.error("Unable to load SQLite JDBC driver", e);
        }

        if (this.connection == null || this.connection.isClosed()) {
            String url = "jdbc:mariadb://" +
                    VelocityEngine.CONFIG.getDatabaseHost() +
                    ":" +
                    VelocityEngine.CONFIG.getDatabasePort() +
                    "/" +
                    VelocityEngine.CONFIG.getDatabaseName();

            this.connection = DriverManager.getConnection(url, VelocityEngine.CONFIG.getDatabaseUsername(), VelocityEngine.CONFIG.getDatabasePassword());
            this.logger.info("Connected to the database");

            initializeDatabase();
        }

        return this.connection;
    }

    public void initializeDatabase() throws SQLException {
        this.logger.info("Initializing database");
        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS core_players (" +
                "playerID VARCHAR(36) PRIMARY KEY, " + // UUID
                "playerUsername VARCHAR(32), " + // Player username
                "playerUsernameLower VARCHAR(32), " + // Player username in lowercase
                "playerNickname VARCHAR(32), " + // Player nickname
                "serverName VARCHAR(38), " + // Player's last server
                "playerFirstIP VARCHAR(40), " + // Player's first IP
                "playerLastIP VARCHAR(40), " + // Player's last IP
                "playerIsOnlineMode BOOLEAN)"; // True if player is premium and false if cracked

        statement.execute(sql);
        statement.close();
    }

    public PlayerModel getPlayer(String playerID) throws SQLException {
        Statement statement = getConnection().createStatement();
        ResultSet results = statement.executeQuery("SELECT * FROM core_players WHERE playerID = '" + playerID + "'");

        if (results.next()) {
            String playerUsername = results.getString("playerUsername");
            String playerUsernameLower = results.getString("playerUsernameLower");
            String playerNickname = results.getString("playerNickname");
            String serverName = results.getString("serverName");
            String playerFirstIP = results.getString("playerFirstIP");
            String playerLastIP = results.getString("playerLastIP");
            boolean playerIsOnlineMode = results.getBoolean("playerIsOnlineMode");

            PlayerModel player = new PlayerModel();
            player.playerID = playerID;
            player.playerUsername = playerUsername;
            player.playerUsernameLower = playerUsernameLower;
            player.playerNickname = playerNickname;
            player.serverName = serverName;
            player.playerFirstIP = playerFirstIP;
            player.playerLastIP = playerLastIP;
            player.playerIsOnlineMode = playerIsOnlineMode;

            statement.close();
            return player;
        }
        statement.close();
        return null;
    }

    public void insetPlayer(PlayerModel player) throws SQLException {
        Statement statement = getConnection().createStatement();
        String statementString = "INSERT INTO core_players (playerID, " +
                "playerUsername, " +
                "playerUsernameLower, " +
                "playerNickname, " +
                "serverName, " +
                "playerFirstIP, " +
                "playerLastIP, " +
                "playerIsOnlineMode) VALUES ('{playerID}', " +
                "'{playerUsername}', " +
                "'{playerUsernameLower}', " +
                "'{playerNickname}', " +
                "'{serverName}', " +
                "'{playerFirstIP}', " +
                "'{playerLastIP}', " +
                "'{playerIsOnlineMode}')";
        statement.executeUpdate(statementString
                .replace("{playerID}", player.playerID)
                .replace("{playerUsername}", player.playerUsername)
                .replace("{playerUsernameLower}", player.playerUsernameLower)
                .replace("{playerNickname}", player.playerNickname)
                .replace("{serverName}", player.serverName)
                .replace("{playerFirstIP}", player.playerFirstIP)
                .replace("{playerLastIP}", player.playerLastIP)
                .replace("{playerIsOnlineMode}", player.playerIsOnlineMode ? "1" : "0"));
        statement.close();
    }

    public void updatePlayer(PlayerModel player) throws SQLException {
        Statement statement = getConnection().createStatement();
        String statementString = "UPDATE core_players SET " +
                "playerUsername = '{playerUsername}', " +
                "playerUsernameLower = '{playerUsernameLower}', " +
                "playerNickname = '{playerNickname}', " +
                "serverName = '{serverName}', " +
                "playerFirstIP = '{playerFirstIP}', " +
                "playerLastIP = '{playerLastIP}', " +
                "playerIsOnlineMode = '{playerIsOnlineMode}' " +
                "WHERE playerID = '{playerID}'";
        statement.executeUpdate(statementString
                .replace("{playerID}", player.playerID)
                .replace("{playerUsername}", player.playerUsername)
                .replace("{playerUsernameLower}", player.playerUsernameLower)
                .replace("{playerNickname}", player.playerNickname)
                .replace("{serverName}", player.serverName)
                .replace("{playerFirstIP}", player.playerFirstIP)
                .replace("{playerLastIP}", player.playerLastIP)
                .replace("{playerIsOnlineMode}", player.playerIsOnlineMode ? "1" : "0"));
        statement.close();
    }

    public static class PlayerModel {
        public String playerID = ""; // UUID of the player
        public String playerUsername = ""; // Username of the player
        public String playerUsernameLower = ""; // Lowercase username of the player
        public String playerNickname = ""; // Nickname of the player
        public String serverName = ""; // Last server the player was online at
        public String playerFirstIP = ""; // First IP of the player
        public String playerLastIP = ""; // Last IP of the player
        public boolean playerIsOnlineMode = false; // Is player an authenticated player
    }
}
