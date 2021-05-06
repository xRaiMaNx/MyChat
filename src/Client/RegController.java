// Окошко регистрации

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
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegController {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML private TextField loginField;
    @FXML private TextField nickField;
    @FXML private PasswordField passwordField;
    @FXML private Button createAccount;
    @FXML private Button signInButton;
    @FXML private Label systemAlert;


    // Открывает окошко авторизации
    private void setAuthWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("auth.fxml"));
        try {
            loader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
        signInButton.getScene().getWindow().hide(); // для небольшой анимации :)
        ClientAppController controller = loader.getController();
        Parent root = loader.getRoot();
        Stage stage = ClientApp.getPrimaryStage();
        stage.setScene(new Scene(root, 891, 860));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    void initialize() {
        // Если нажали на Sign In
        signInButton.setOnAction(event -> {
            setAuthWindow();
        });
        // Если нажали на Create account
        createAccount.setOnAction(event -> {
            // Вытаскиваем данные из полей
            String login = loginField.getText().trim();
            String nick = nickField.getText().trim();
            String pass = passwordField.getText().trim();
            // Анимации тряски полей
            Shake userLoginAnim = new Shake(loginField);
            Shake userNickAnim = new Shake(nickField);
            Shake userPassAnim = new Shake(passwordField);

            if (login.equals("") || nick.equals("") || pass.equals("")) {
                systemAlert.setText("Enter all fields");
                if (login.equals(""))
                    userLoginAnim.playAnim();
                if (nick.equals(""))
                    userNickAnim.playAnim();
                if (pass.equals(""))
                    userPassAnim.playAnim();
                return;
            }
            // Incorrect Login
            if (login.length() < 6 || login.length() > 20) {
                systemAlert.setText("Login must contain from 6 to 20 characters");
                userLoginAnim.playAnim(); // Трясем трясем
                return;
            }
            // Incorrect Nick
            if (nick.length() < 1 || nick.length() > 30) {
                systemAlert.setText("Nick must contain from 1 to 30 characters");
                userNickAnim.playAnim(); // Трясем трясем
                return;
            }
            // Incorrect Pass
            if (pass.length() < 6 || pass.length() > 30) {
                systemAlert.setText("Password must contain from 6 to 30 characters");
                userNickAnim.playAnim(); // Трясем трясем
                return;
            }
            // Если все ок, то пробуем зарегестрировать
            onRegClick("/reg " + login + " " + nick + " " + pass);
        });
    }

    // Подключаемся к серверу
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

    // Если все введенные данные корректны, то пробуем зарегестрировать
    private void onRegClick(String mes) {
        // Подключаемся к серверу
        if (socket == null || socket.isClosed()) {
            start();
        }
        try {
            out.writeUTF(mes); // Отсылаем серверу сообщение
            while(true) {
                String str = in.readUTF().trim(); // Считываем сообщение от сервера
                if(str.startsWith("/regok")) { // Если regok, то говорим пользователю, что зарегестрировали
                    systemAlert.setTextFill(Paint.valueOf("#0fff22"));
                    systemAlert.setText("Successfully");
                    out.writeUTF("/end");
                    closeConnection();
                    setAuthWindow();
                    break;
                } // Если regfalse, то говорим пользователю, что логин/ник некорректны
                else if(str.equals("/regfalse")) {
                    // Анимации тряски
                    Shake userLoginAnim = new Shake(loginField);
                    Shake userNickAnim = new Shake(nickField);
                    userLoginAnim.playAnim();
                    userNickAnim.playAnim();

                    systemAlert.setText("This login/nick already exists");
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

    // Закрываем подключение к серверу
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
