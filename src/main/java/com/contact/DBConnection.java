package com.contact;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection
 * ------------
 * Utility class that provides a JDBC connection to the MySQL database.
 *
 * Update DB_URL / DB_USER / DB_PASSWORD to match your local MySQL setup.
 */
public class DBConnection {

    // ── Connection parameters ─────────────────────────────────────────────
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/contact_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER     = "root";       // change if needed
    private static final String DB_PASSWORD = "";           // no password set

    /**
     * Returns a new Connection to contact_db.
     * The caller is responsible for closing the connection.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load the MySQL JDBC driver (good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
