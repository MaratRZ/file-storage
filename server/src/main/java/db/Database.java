package db;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class Database {
    public static Connection conn;
    public static PreparedStatement stmt;
    public static ResultSet rs;

    public static void openDB() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:./server/db/file-storage.db3");
        log.debug("Database is open");
    }

    public static void closeDB() throws SQLException  {
        conn.close();
        stmt.close();
        rs.close();
        log.debug("Database is closed");
    }

    public static String getUsername(String login, String password) throws SQLException {
        if (conn == null || conn.isClosed()) return null;
        stmt = conn.prepareStatement("select username from users where login = ? and pass = ?");
        stmt.setString(1, login);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("username");
        } else {
            return null;
        }
    }
}
