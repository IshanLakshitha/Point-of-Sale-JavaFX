package lk.ijse.fx.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if(connection == null){
         Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1234");
        }
        return connection;
    }
}
