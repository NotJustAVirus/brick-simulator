package web.brick;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final String DATABASE_URL;
    private Connection connection;

    private DatabaseManager() {
        DATABASE_URL = System.getenv("BRICK_DB_URL");
        String user = System.getenv("BRICK_DB_USER");
        String password = System.getenv("BRICK_DB_PASSWORD");
        try {
            Properties prop = new Properties();
            prop.put("user", user);
            prop.put("password", password);
            prop.put("autoReconnect", "true");
            prop.put("maxReconnects", "5");
            prop.put("initialTimeout", "5");
            connection = DriverManager.getConnection(
                DATABASE_URL, prop);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null || instance.connection == null) {
            instance = new DatabaseManager();
        } else {
            try {
                if (!instance.connection.isValid(1)) {
                    instance = new DatabaseManager();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    
    public boolean setUserTime(String uuid, long time) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users (uuid, time) VALUES (?, ?) ON DUPLICATE KEY UPDATE time = ?;");
            statement.setString(1, uuid);
            statement.setLong(2, time);
            statement.setLong(3, time);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getUserTime(String uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM users WHERE uuid = ?");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getTotalTime() {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(
                "SELECT SUM(time) FROM users");
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
