package Server;

import java.util.*;
import java.net.*;
import java.io.*;

public class MyServer {

    private final int PORT = 8189; // порт
    private List<ClientHandler> clients; // список клиентов, подключившихся к серверу
    private AuthService authService; // AuthService - интерфейс для BaseAuthService

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        //try-catch с ресурсами (открываем сокет)
        try (ServerSocket server = new ServerSocket(PORT)) {
            // Создаем объект класса, который обрабатывает запросы, направляемые в класс,
            // который взаимодействует с базой данных
            authService = new BaseAuthService();
            authService.start(); // Покажем серверу, что объект BaseAuthService удалось создать
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept(); // Ожидаем нового клиента
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket); // Клиент подключился => создадим обработчик этого клиента
            }
        } catch (IOException e) {
            System.out.println("Ошибка в работе сервера");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    // Запускается из ClientHandler только в том случае, когда
    // клиент ввел все данные, и они оказались верный. Проверяет
    // есть ли в сети пользователь с таким ником
    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    // Отправляет всем пользователям сообщение
    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    // Отправляет конкретному пользователю личное сообщение через команду /w в чате
    public synchronized void personalMsg(String msg, String name) {
        String[] str = msg.split(" ");
        ClientHandler a = null;
        boolean flag = false;
        for (ClientHandler o : clients) {
            if(name.equals(o.getName())) a = o;
            if(str[1].compareToIgnoreCase(o.getName()) == 0) {
                msg = msg.replace("/w " + str[1] + " ", "");
                o.sendMsg("ЛС от " + name + ": " + msg);
                flag = true;
            }
        }
        if (flag) a.sendMsg("Вы шепчете " + str[1] + ": " + msg);
        else a.sendMsg("Пользователь с данным ником не найден или не в сети");
    }

    // Выписывает клиента из списка клиентов, которые смогли авторизоваться
    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
    }

    // Записывает клиента в список клиентов, который смогли авторизоваться
    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }
}
