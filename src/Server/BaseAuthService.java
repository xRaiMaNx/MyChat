package Server;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseAuthService implements AuthService {

    DataBaseHandler dbh;

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }


    // подключаемся к базе данных, создавая объект обработчика запросов в базу данных
    public BaseAuthService() {
        dbh = new DataBaseHandler();
    }

    @Override
    public boolean reg(String login, String nick, String pass) {
        // вызываем функцию в DataBaseHandler, которая проверяет есть ли такой логин или ник
        // возвращает false, если ника или логина нету
        if (!dbh.isLoginOrNickBusy(login, nick)) {
            dbh.signUpUser(login, nick, pass);
            return true;
        }
        return false;
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        ResultSet result = dbh.getUser(login, pass);
        String nick = null;
        try {
            if (result.next()) {
                nick = result.getString(3);
                result.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nick;
    }
}
