package com.capstone.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=GoToTheSupermarketHelpingYou";
    private static final String USER = "sqlserver";
    private static final String PASSWORD = "sqlserver";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
