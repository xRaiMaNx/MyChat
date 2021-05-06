package Server;

import java.sql.SQLException;

public interface AuthService {
    void start();
    // Эта функция нужна для окна авторизации. Проверяет корректно ли введен пароль и логин
    // Возвращает null, если такого login не существует или некорректный пароль
    String getNickByLoginPass(String login, String pass) throws SQLException, ClassNotFoundException;
    // Выдаем true, если пользователя удалось зарегистрировать
    // false иначе (либо логин, либо ник уже существуют)
    boolean reg(String login, String nick, String pass);
    void stop();
}
