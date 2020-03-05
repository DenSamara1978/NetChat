package server;

import java.sql.*;

import static java.lang.Class.forName;

public class DB {
    private static Connection connection;
    private static Statement stmt;

    public static boolean open ()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection ( "jdbc:sqlite:netchat" );
            stmt = connection.createStatement ();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void close ()
    {
        try {
            stmt.close ();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close ();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean execute ( String sql )
    {
        try {
            return ( stmt.executeUpdate ( sql ) != 0 );
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String querySingleResultString ( String sql )
    {
        try {
            ResultSet rs = stmt.executeQuery ( sql );
            if ( rs.next())
                return rs.getString ( 1 );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
