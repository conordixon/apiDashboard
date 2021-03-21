package PostgresSQLConnector;

import java.sql.Connection;
import java.sql.DriverManager;


public class PostgresSQLConnector {

    private static Connection conn = null;

    static {
        String url = ("jdbc:postgresql://localhost:5432/postgres");
//        String user = ("conordixon");
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
