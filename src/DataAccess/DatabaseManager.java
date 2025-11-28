package DataAccess;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * DatabaseManager - MySQL Database Connection Management
 * Handle connection pooling, queries, dan database operations
 */
public class DatabaseManager {
    private static Connection connection;
    private static String host = "localhost";
    private static String port = "3306";
    private static String database = "pitbullgym";
    private static String username = "root";
    private static String password = "";

    /**
     * Load configuration dari config.properties file
     */
    private static void loadConfiguration() {
        try {
            Properties props = new Properties();
            File configFile = new File("src/resources/config.properties");
            
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                    host = props.getProperty("db.host", "localhost");
                    port = props.getProperty("db.port", "3306");
                    database = props.getProperty("db.name", "pitbullgym");
                    username = props.getProperty("db.user", "root");
                    password = props.getProperty("db.password", "");
                    System.out.println("✓ Configuration loaded from config.properties");
                }
            } else {
                System.out.println("⚠ config.properties not found, using default configuration");
            }
        } catch (IOException e) {
            System.err.println("⚠ Error loading configuration: " + e.getMessage());
        }
    }

    /**
     * Initialize MySQL database connection
     */
    public static void initialize() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL JDBC driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC driver not found!");
            System.err.println("Please add mysql-connector-java jar to classpath");
            return;
        }

        // Load configuration
        loadConfiguration();

        try {
            // Build connection URL
            String jdbcUrl = String.format(
                    "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8",
                    host, port, database
            );

            // Attempt connection
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("✓ Connected to MySQL database: " + database + " on " + host + ":" + port);

            // Create tables if not exist
            createTablesIfNotExist();
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to MySQL database: " + e.getMessage());
            System.err.println("Please make sure MySQL is running and database exists");
            e.printStackTrace();
        }
    }

    /**
     * Create tables if they don't exist
     */
    private static void createTablesIfNotExist() {
        String createMembersTable = "CREATE TABLE IF NOT EXISTS members (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "phone VARCHAR(20) NOT NULL UNIQUE," +
                "plan_type VARCHAR(50) NOT NULL," +
                "start_date DATE NOT NULL," +
                "end_date DATE NOT NULL," +
                "status VARCHAR(20) NOT NULL," +
                "membership_count INT DEFAULT 1," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "INDEX idx_name (name)," +
                "INDEX idx_phone (phone)," +
                "INDEX idx_status (status)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createMembersTable);
            System.out.println("✓ Members table checked/created successfully");
        } catch (SQLException e) {
            System.err.println("✗ Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get database connection
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                // Attempt to reinitialize connection
                System.out.println("⚠ Database connection is null/closed/invalid, reinitializing...");
                initialize();
            }
        } catch (SQLException e) {
            System.err.println("✗ Error checking connection validity: " + e.getMessage());
            initialize();
        }

        return connection;
    }

    /**
     * Check if database connection is active
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Execute update query (INSERT, UPDATE, DELETE)
     */
    public static int executeUpdate(String query, Object... params) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        }
    }

    /**
     * Execute query dan return ResultSet
     */
    public static ResultSet executeQuery(String query, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }

    /**
     * Get connection configuration info
     */
    public static String getConnectionInfo() {
        return String.format("MySQL - Host: %s:%s, Database: %s, User: %s", 
                host, port, database, username);
    }

    /**
     * Print database info
     */
    public static void printDatabaseInfo() {
        System.out.println("\n========== DATABASE INFO ==========");
        System.out.println("Type: MySQL");
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Database: " + database);
        System.out.println("User: " + username);
        System.out.println("Status: " + (isConnected() ? "Connected ✓" : "Disconnected ✗"));
        System.out.println("===================================\n");
    }
}
