package PostgresSQLConnector;

import java.sql.Connection;
import java.sql.DriverManager;

/*
https://jdbc.postgresql.org/documentation/head/connect.html
https://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html
PostgreSQL JDBC driver integation for PostgreSQL local database connection.
*/

public class PostgresSQLConnector {

    private static Connection conn = null;

    static {
        String url = ("jdbc:postgresql://localhost:5432/postgres");
        String database = ("postgres");
        String password = ("password");
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, database, password);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        return conn;
    }

}
