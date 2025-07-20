package br.com.cajuajuda.cajuajudadesktop;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    public static String authToken = null;

    @FXML
    protected void handleLoginButtonAction() {
        String email = emailField.getText();
        String senha = passwordField.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            statusLabel.setText("E-mail e senha são obrigatórios.");
            return;
        }

        statusLabel.setText("Autenticando...");

        new Thread(() -> {
            try {
                String jsonBody = String.format("{\"email\":\"%s\", \"senha\":\"%s\"}", email, senha);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/auth/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> responseMap = mapper.readValue(response.body(), Map.class);
                    authToken = responseMap.get("token");

                    System.out.println("Token JWT recebido com sucesso: " + authToken);

                    // ===================================================================
                    // NOVO CÓDIGO DE NAVEGAÇÃO
                    // ===================================================================
                    Platform.runLater(() -> {
                        try {
                            // 1. Carrega a nova tela de chamados
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("chamados-view.fxml"));
                            Scene scene = new Scene(loader.load());

                            // 2. Cria uma nova janela (Stage) para a tela principal
                            Stage mainStage = new Stage();
                            mainStage.setTitle("Caju Ajuda - Painel do Técnico");
                            mainStage.setScene(scene);
                            mainStage.show();

                            // 3. Fecha a janela de login atual
                            Stage loginStage = (Stage) emailField.getScene().getWindow();
                            loginStage.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                            statusLabel.setText("Erro ao carregar a tela principal.");
                        }
                    });

                } else {
                    Platform.runLater(() -> statusLabel.setText("Falha no login: E-mail ou senha inválidos."));
                }

            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Erro de conexão com o servidor."));
                e.printStackTrace();
            }
        }).start();
    }
}