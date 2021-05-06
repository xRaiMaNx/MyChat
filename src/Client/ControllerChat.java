// Контроллер самого чата

package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ControllerChat {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String myNick;


    // Принимает подключение к серверу
    public void set(Socket socket, DataInputStream in, DataOutputStream out, String myNick) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.myNick = myNick;
    }

    @FXML private TextArea mesArea;
    @FXML private Button sendButton;
    @FXML private TextArea chatArea;

    @FXML
    void initialize() {
        // Если пользователь закрыл окошко
        ClientApp.getPrimaryStage().setOnCloseRequest(event -> {
            try {
                out.writeUTF("/end");
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
        // Поток для получения сообщений
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if(in != null) {
                            String strFromServer = in.readUTF();
                            if(strFromServer != null) {
                                chatArea.appendText(strFromServer + "\n");
                            }
                            if (strFromServer.trim().equals("/end")) {
                                Platform.exit();
                                System.exit(0);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }
        });
        t.start();

        // Если нажал на кнопку отправки сообщения
        sendButton.setOnAction(event -> {
            try {
                String answer = mesArea.getText();
                if(!answer.equals(null) && !answer.trim().equals("")) {
                    out.writeUTF(answer.trim());
                }
                mesArea.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
