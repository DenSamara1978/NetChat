package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService
{
    private boolean dbConnected;

    public SimpleAuthService()
    {
        dbConnected = DB.open();
        if ( dbConnected )
            System.out.println( "База данных подключена." );
        else
            System.out.println( "Ошибка подключени к базе данных!" );
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password)
    {
        if ( dbConnected )
            return DB.querySingleResultString ( "SELECT nickname FROM users WHERE login=" + login + " AND password=" + password );
        else
            return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname)
    {
        if (password.trim().equals("")) {
            return false;
        }

        if ( dbConnected )
            return DB.execute ( "INSERT INTO users ( login, password, nickname ) VALUES ( " + login + ", " + password + ", " + nickname + " )" );
        else
            return false;
    }
}
