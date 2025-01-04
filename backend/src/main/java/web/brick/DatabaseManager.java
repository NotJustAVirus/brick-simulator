package web.brick;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DATABASE_URL = "jdbc:sqlite:database.db";
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/brick_db", "root", "admin");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public boolean addUser(String uuid, long time) {
        try {
            connection.createStatement().executeUpdate(
                "INSERT INTO users (uuid, time) VALUES ('" + uuid + "', " + time + ")");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(String uuid, long time) {
        try {
            connection.createStatement().executeUpdate(
                "UPDATE users SET time = " + time + " WHERE uuid = '" + uuid + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getUser(String uuid) {
        try {
            var resultSet = connection.createStatement().executeQuery(
                "SELECT * FROM users WHERE uuid = '" + uuid + "'");
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
            var resultSet = connection.createStatement().executeQuery(
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
