package org.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        openLogin();
    }

    public static void openLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getClassLoader().getResource("ui/LoginView.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void openRegister() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getClassLoader().getResource("ui/RegisterView.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.setTitle("注册 - Chamber");
        primaryStage.show();
    }

    public static void openMain() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getClassLoader().getResource("ui/MainView.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Chamber - 主界面");
        primaryStage.show();
    }
}
