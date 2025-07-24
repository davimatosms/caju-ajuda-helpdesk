package br.com.cajuajuda.cajuajudadesktop.controller;

import br.com.cajuajuda.cajuajudadesktop.MainApp;
import br.com.cajuajuda.cajuajudadesktop.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    public static String authToken;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    private final ApiService apiService = new ApiService(); // <-- USA O SERVIÇO

    @FXML
    private void handleLoginButtonAction() {
        String email = emailField.getText();
        String senha = passwordField.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            statusLabel.setText("E-mail e senha são obrigatórios.");
            return;
        }

        statusLabel.setText("Autenticando...");

        new Thread(() -> {
            // A lógica de HTTP agora está no ApiService
            String token = apiService.authenticate(email, senha);

            Platform.runLater(() -> {
                if (token != null) {
                    authToken = token;
                    System.out.println("Token JWT recebido com sucesso: " + authToken);
                    abrirTelaPrincipal();
                } else {
                    statusLabel.setText("Falha na autenticação. Verifique suas credenciais.");
                    authToken = null;
                }
            });
        }).start();
    }

    private void abrirTelaPrincipal() {
        try {
            // Caminho corrigido para o FXML
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("chamados-view.fxml"));
            Parent root = loader.load();

            String css = MainApp.class.getResource("styles.css").toExternalForm();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(css);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Caju Ajuda - Painel do Técnico");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erro ao carregar a tela principal.");
        }
    }
}