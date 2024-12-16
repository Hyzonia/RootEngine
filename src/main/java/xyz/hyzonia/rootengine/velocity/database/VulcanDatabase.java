package xyz.hyzonia.rootengine.velocity.database;

import org.slf4j.Logger;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VulcanDatabase {
    private final Logger logger;
    private Connection connection;

    public VulcanDatabase(Logger logger) {
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
        String sql = "CREATE TABLE vulcan_alerts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "check_name VARCHAR(255)," +
                "check_type VARCHAR(255)," +
                "vl INT," +
                "player VARCHAR(255)," +
                "max_vl INT," +
                "server_name VARCHAR(255)," +
                "client_version VARCHAR(255)," +
                "tps FLOAT," +
                "ping INT," +
                "description TEXT," +
                "info TEXT," +
                "dev VARCHAR(255)," +
                "severity VARCHAR(255)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        statement.execute(sql);
        statement.close();
    }

    public void insertVulcanAlert(VulcanAlert alert) throws SQLException {
        String sql = "INSERT INTO vulcan_alerts (check_name, check_type, vl, player, max_vl, server_name, client_version, tps, ping, description, info, dev, severity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, alert.checkName);
            stmt.setString(2, alert.checkType);
            stmt.setInt(3, alert.vl);
            stmt.setString(4, alert.player);
            stmt.setInt(5, alert.maxVl);
            stmt.setString(6, alert.serverName);
            stmt.setString(7, alert.clientVersion);
            stmt.setFloat(8, alert.tps);
            stmt.setInt(9, alert.ping);
            stmt.setString(10, alert.description);
            stmt.setString(11, alert.info);
            stmt.setString(12, alert.dev);
            stmt.setString(13, alert.severity);
            stmt.executeUpdate();
        }
    }

    public List<VulcanAlert> getVulcanAlerts() throws SQLException {
        List<VulcanAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM vulcan_alerts";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                VulcanAlert alert = new VulcanAlert(
                        rs.getString("check_name"),
                        rs.getString("check_type"),
                        rs.getInt("vl"),
                        rs.getString("player"),
                        rs.getInt("max_vl"),
                        rs.getString("server_name"),
                        rs.getString("client_version"),
                        rs.getFloat("tps"),
                        rs.getInt("ping"),
                        rs.getString("description"),
                        rs.getString("info"),
                        rs.getString("dev"),
                        rs.getString("severity"),
                        rs.getTimestamp("created_at")
                );
                alerts.add(alert);
            }
        }
        return alerts;
    }

    public void deleteVulcanAlertById(int id) throws SQLException {
        String sql = "DELETE FROM vulcan_alerts WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static class VulcanAlert {
        public String checkName;
        public String checkType;
        public int vl;
        public String player;
        public int maxVl;
        public String serverName;
        public String clientVersion;
        public float tps;
        public int ping;
        public String description;
        public String info;
        public String dev;
        public String severity;
        public Timestamp createdAt;

        public VulcanAlert(String checkName, String checkType, int vl, String player, int maxVl, String serverName, String clientVersion, float tps, int ping, String description, String info, String dev, String severity, Timestamp createdAt) {
            this.checkName = checkName;
            this.checkType = checkType;
            this.vl = vl;
            this.player = player;
            this.maxVl = maxVl;
            this.serverName = serverName;
            this.clientVersion = clientVersion;
            this.tps = tps;
            this.ping = ping;
            this.description = description;
            this.info = info;
            this.dev = dev;
            this.severity = severity;
            this.createdAt = createdAt;
        }
    }
}
