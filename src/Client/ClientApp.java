package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private static Stage primaryStage;

    private void setPrimaryStage(Stage stage) {
        ClientApp.primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return ClientApp.primaryStage;
    }

    // Создаем окошко авторизации
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("auth.fxml"));
        setPrimaryStage(primaryStage);
        primaryStage.setScene(new Scene(root, 891, 860));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
