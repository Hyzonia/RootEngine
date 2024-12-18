package xyz.hyzonia.rootengine.velocity.database;

import org.slf4j.Logger;
import xyz.hyzonia.rootengine.velocity.VelocityEngine;

import java.sql.*;

public class ReportDatabase {
    private final Logger logger;
    private Connection connection;

    public ReportDatabase(Logger logger) {
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
        String sql = "CREATE TABLE IF NOT EXISTS core_reports (" +
                "reportID VARCHAR(36) PRIMARY KEY, " +
                "reporter VARCHAR(32), " +
                "accused VARCHAR(32), " +
                "serverName VARCHAR(38), " +
                "reportType VARCHAR(32))";
        statement.execute(sql);
        statement.close();
    }

    public ReportModel getReport(String reportID) throws SQLException {
        Statement statement = getConnection().createStatement();
        ResultSet results = statement.executeQuery("SELECT * FROM core_reports WHERE reportID = '" + reportID + "'");

        if (results.next()) {
            String reporter = results.getString("reporter");
            String reportType = results.getString("reportType");
            String accused = results.getString("accused");
            String serverName = results.getString("serverName");

            ReportModel report = new ReportModel();
            report.reportID = reportID;
            report.reporter = reporter;
            report.accused = accused;
            report.serverName = serverName;
            report.reportType = reportType;

            statement.close();
            return report;
        }
        statement.close();
        return null;
    }

    public void insertReport(ReportModel report) throws SQLException {
        Statement statement = getConnection().createStatement();
        String statementString = "INSERT INTO core_reports (reportID, reporter, accused, serverName, reportType) VALUES ('{reportID}', '{reporter}', '{accused}', '{serverName}', '{reportType}')";
        statement.executeUpdate(statementString
                .replace("{reportID}", report.reportID)
                .replace("{reporter}", report.reporter)
                .replace("{accused}", report.accused)
                .replace("{serverName}", report.serverName)
                .replace("{reportType}", report.reportType));
        statement.close();
    }

    public static class ReportModel {
        public String reportID; // the UUID of the report
        public String reporter; // the player reporting
        public String accused; // the player reported
        public String serverName; // the server where the player reported is on
        public String reportType; // the report type from ReportType class
    }
}
