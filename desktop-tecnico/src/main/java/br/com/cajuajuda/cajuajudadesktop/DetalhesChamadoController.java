package br.com.cajuajuda.cajuajudadesktop;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DetalhesChamadoController {

    private Long chamadoId;
    private final ApiService apiService = new ApiService();
    private final WebSocketService webSocketService = new WebSocketService();

    @FXML private Label tituloLabel;
    @FXML private ListView<String> mensagensListView;
    @FXML private TextArea respostaArea;
    @FXML private Button enviarButton;

    @FXML
    public void initialize() {
        // Garante que a conexão WebSocket seja fechada ao fechar a janela
        enviarButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs2, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnCloseRequest(event -> webSocketService.disconnect());
                    }
                });
            }
        });
    }


    public void carregarDadosIniciais(Long chamadoId) {
        this.chamadoId = chamadoId;
        this.tituloLabel.setText("Carregando chamado #" + chamadoId + "...");
        carregarHistorico();

        String token = LoginController.authToken;
        webSocketService.connect(token, this.chamadoId, this::onMessageReceived); // Garanta que o token está sendo passado
    }

    private void onMessageReceived(MensagemDto mensagem) {
        Platform.runLater(() -> {
            adicionarMensagemNaLista(mensagem.getAutor().getNome(), mensagem.getDataEnvio(), mensagem.getTexto());
        });
    }

    private void carregarHistorico() {
        new Thread(() -> {
            String token = LoginController.authToken;
            apiService.getChamadoById(this.chamadoId, token).ifPresent(chamado -> {
                Platform.runLater(() -> popularCampos(chamado));
            });
        }).start();
    }

    private void popularCampos(Chamado chamado) {
        tituloLabel.setText(chamado.getTitulo());
        List<String> conversaFormatada = new ArrayList<>();
        if (chamado.getCliente() != null) {
            conversaFormatada.add(formatarMensagem(chamado.getCliente().getNome(), chamado.getDataCriacao(), chamado.getDescricao()));
        }
        if (chamado.getMensagens() != null) {
            for (Mensagem msg : chamado.getMensagens()) {
                if(msg.getAutor() != null) {
                    conversaFormatada.add(formatarMensagem(msg.getAutor().getNome(), msg.getDataEnvio(), msg.getTexto()));
                }
            }
        }
        mensagensListView.setItems(FXCollections.observableArrayList(conversaFormatada));
        mensagensListView.scrollTo(mensagensListView.getItems().size() - 1);
    }

    private String formatarMensagem(String autor, java.time.LocalDateTime data, String texto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("[%s] %s:\n%s\n", data.format(formatter), autor, texto);
    }

    private void adicionarMensagemNaLista(String autor, java.time.LocalDateTime data, String texto) {
        mensagensListView.getItems().add(formatarMensagem(autor, data, texto));
        mensagensListView.scrollTo(mensagensListView.getItems().size() - 1);
    }

    @FXML
    private void handleEnviarMensagem() {
        String textoResposta = respostaArea.getText();
        if (textoResposta == null || textoResposta.isBlank()) {
            return;
        }
        webSocketService.sendMessage(textoResposta, this.chamadoId);
        respostaArea.clear();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
} // <-- ESTA CHAVE ESTAVA FALTANDO