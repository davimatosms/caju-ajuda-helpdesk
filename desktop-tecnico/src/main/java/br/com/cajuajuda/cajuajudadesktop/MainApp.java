package br.com.cajuajuda.cajuajudadesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) { // Note que removemos o 'throws IOException'
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            stage.setTitle("Caju Ajuda - Login Técnico");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            // Se qualquer erro ocorrer no bloco 'try', ele será capturado aqui
            System.err.println("Ocorreu um erro crítico ao iniciar a aplicação.");
            // A linha abaixo é a mais importante: ela imprime o erro completo no console
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}