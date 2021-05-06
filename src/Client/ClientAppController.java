// Контроллер окна авторизации

package Client;

import Client.animations.Shake;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientAppController {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String myNick;

    // Различные аттрибуты из интерфейса авторизации
    @FXML private TextField loginField; // Тектовое поле логина
    @FXML private PasswordField passwordField; // Поле пароля
    @FXML private Button authSignInButton; // Кнопка Log In
    @FXML private Button signUpButton; // Кнопка SignUp
    @FXML private Label systemAlert; // невидимое поле, которое выдает системные сообщения пользователю

    @FXML
    void initialize() {
        // Если нажали на кнопку Log In
        authSignInButton.setOnAction(event -> {
            String login = loginField.getText().trim();
            String pass = passwordField.getText().trim();
            // Создаем анимации тряски полей, если что-то неверно
            Shake userLoginAnim = new Shake(loginField);
            Shake userPassAnim = new Shake(passwordField);
            if(login.equals("") && pass.equals("")) { // Если оба поля пусты
                systemAlert.setText("Enter login and pass");
                userLoginAnim.playAnim();
                userPassAnim.playAnim();
            } else if(login.equals("")) { // Если только логин пустой
                systemAlert.setText("Enter login");
                userLoginAnim.playAnim();
            } else if(pass.equals("")) { // Если только пароль пустой
                systemAlert.setText("Enter pass");
                userPassAnim.playAnim();
            } else { // Eсли все ок, то пробуем авторизировать
                onAuthClick("/auth " + login + " " + pass);
            }
        });
        // Если нажали на кнопку Sign Up
        signUpButton.setOnAction(event -> {
            // Открываем окошко регистрации
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("reg.fxml"));
            try {
                loader.load();
            } catch(IOException e) {
                e.printStackTrace();
            }
            signUpButton.getScene().getWindow().hide(); // для небольшой анимации перед новым окном :)
            RegController controller = loader.getController();
            Parent root = loader.getRoot();
            Stage stage = ClientApp.getPrimaryStage();
            stage.setScene(new Scene(root, 891, 860));
            stage.setResizable(false);
            stage.show();
        });

    }

    // Соединяемся с сервером
    private void start() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            systemAlert.setText("Could not connect to server");
            e.printStackTrace();
        }
    }

    // Если кликнули Log In и все ок
    private void onAuthClick(String mes) {
        // Подключаемся к серверу
        if (socket == null || socket.isClosed()) {
            start();
        }
        try {
            out.writeUTF(mes);
            while(true) {
                String str = in.readUTF().trim(); // Читаем что прислал сервер
                if(str.startsWith("/authok ")) { // Если authok, то сервер нас авторизовал
                    myNick = str.split(" ")[1];
                    // Открываем окошко чата
                    authSignInButton.getScene().getWindow().hide(); // опять же для анимации
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("chat.fxml"));
                    try {
                        loader.load();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    ControllerChat controller = loader.getController();
                    // Передаем контроллеру подключение к серверу
                    controller.set(socket, in, out, myNick);
                    Parent root = loader.getRoot();
                    Stage stage = ClientApp.getPrimaryStage();
                    stage.setScene(new Scene(root, 656, 790));
                    stage.setResizable(false);
                    stage.show();
                    break;
                } else if(str.equals("Invalid login/pass") || str.equals("Account already in use")) {
                    // Потрясем пользователю неправильные поля
                    Shake userLoginAnim = new Shake(loginField);
                    Shake userPassAnim = new Shake(passwordField);
                    userLoginAnim.playAnim();
                    userPassAnim.playAnim();
                    systemAlert.setText(str);
                    passwordField.setText("");
                    out.writeUTF("/end");
                    closeConnection();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    // Закрываем соединение с сервером
    private void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
