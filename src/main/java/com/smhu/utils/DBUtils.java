package com.smhu.utils;

import com.smhu.helper.PropertiesWithJavaConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

//    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=GoToTheSupermarketHelpingYou;Trusted_Connection=False;Encrypt=True;";
//    private static final String URL = "jdbc:sqlserver://smhu.database.windows.net:1433;database=GoToTheSupermarketHelpingYou;"
//            + "user=sqlserver@smhu;password=@sql123456;encrypt=true;trustServerCertificate=false;"
//            + "hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
//    private static final String USER = "sqlserver";
//    private static final String PASSWORD = "sqlserver";
//    private static final String PASSWORD = "@sql123456";
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        String url = (String) PropertiesWithJavaConfig.PROPERTIES.get("spring.datasource.url");
        String user = (String) PropertiesWithJavaConfig.PROPERTIES.getProperty("spring.datasource.username", "sa");
        String password = (String) PropertiesWithJavaConfig.PROPERTIES.getProperty("spring.datasource.password", "12345678");
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, user, password);
    }
}
