package Server;

import java.sql.*;

public class DataBaseHandler extends Configs {

    // Подключаем базу данных Postgresql
    private Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(connectionString, dbUser, dbPass);
    }

    // Регистрирует пользователя
    public void signUpUser(String login, String nick, String password) {
        String insert = "INSERT INTO " + Const.USER_TABLE + " (" + Const.USERS_LOGIN +
                        ", " + Const.USERS_PASS + ", " + Const.USERS_NICK +
                        ") VALUES(?,?,?)";
        PreparedStatement prSt = null;
        try {
            prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, login);
            prSt.setString(2, password);
            prSt.setString(3, nick);
            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Выдает пользователя по логину и паролю
    public ResultSet getUser(String login, String password) {
        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                        Const.USERS_LOGIN + "=? AND " + Const.USERS_PASS + "=?";
        PreparedStatement prSt = null;
        try {
            prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, login);
            prSt.setString(2, password);
            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resSet;
    }

    // Проверяет есть ли пользователь с таким ником или логином
    public boolean isLoginOrNickBusy(String login, String nick) {
        ResultSet resSetLogin = null, resSetNick = null;
        String selectLogin = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                             Const.USERS_LOGIN + "=?";
        String selectNick = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                             Const.USERS_NICK + "=?";
        try {
            PreparedStatement prStLogin = getDbConnection().prepareStatement(selectLogin);
            prStLogin.setString(1, login);
            resSetLogin = prStLogin.executeQuery();
            PreparedStatement prStNick = getDbConnection().prepareStatement(selectNick);
            prStNick.setString(1, nick);
            resSetNick = prStNick.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (resSetLogin.next() || resSetNick.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
