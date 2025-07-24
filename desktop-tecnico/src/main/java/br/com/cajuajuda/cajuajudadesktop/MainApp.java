package br.com.cajuajuda.cajuajudadesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);

            // AQUI ADICIONAMOS A FOLHA DE ESTILOS
            String css = this.getClass().getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setTitle("Caju Ajuda - Login Técnico");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Ocorreu um erro crítico ao iniciar a aplicação.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}