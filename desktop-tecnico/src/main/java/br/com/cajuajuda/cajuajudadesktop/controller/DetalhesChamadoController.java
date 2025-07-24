package br.com.cajuajuda.cajuajudadesktop.controller;

import br.com.cajuajuda.cajuajudadesktop.dto.DetalhesChamadoDto;
import br.com.cajuajuda.cajuajudadesktop.dto.MensagemDto;
import br.com.cajuajuda.cajuajudadesktop.dto.StatusChamado;
import br.com.cajuajuda.cajuajudadesktop.dto.StatusUpdateDto;
import br.com.cajuajuda.cajuajudadesktop.service.ApiService;
import br.com.cajuajuda.cajuajudadesktop.service.WebSocketService;
import br.com.cajuajuda.cajuajudadesktop.view.ChatMessageCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class DetalhesChamadoController {

    private Long chamadoId;
    private final ApiService apiService = new ApiService();
    private final WebSocketService webSocketService = new WebSocketService();

    @FXML private Label tituloLabel;
    @FXML private Label labelSlaStatus;
    // --- CORREÇÃO 1: Mudar o tipo da ListView ---
    @FXML private ListView<MensagemDto> mensagensListView;
    @FXML private TextArea respostaArea;
    @FXML private Button enviarButton;
    @FXML private ComboBox<StatusChamado> statusComboBox;
    @FXML private Button salvarStatusButton;

    @FXML
    public void initialize() {
        // --- CORREÇÃO 2: A chamada agora está correta e não dará mais erro ---
        mensagensListView.setCellFactory(param -> new ChatMessageCell());

        statusComboBox.setItems(FXCollections.observableArrayList(StatusChamado.values()));

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
        if (token != null) {
            webSocketService.connect(token, this.chamadoId, this::onMessageReceived);
        }
    }

    // Este método já recebe um MensagemDto, então a lógica fica mais simples
    private void onMessageReceived(MensagemDto mensagemDto) {
        Platform.runLater(() -> {
            // --- CORREÇÃO 3: Adicionar o DTO diretamente, sem conversão ---
            mensagensListView.getItems().add(mensagemDto);
            mensagensListView.scrollTo(mensagensListView.getItems().size() - 1);
        });
    }

    private void carregarHistorico() {
        new Thread(() -> {
            String token = LoginController.authToken;
            // O ApiService precisa retornar um DTO de detalhes, não a entidade
            apiService.getDetalhesChamadoById(this.chamadoId, token).ifPresent(chamadoDto -> {
                Platform.runLater(() -> popularCampos(chamadoDto));
            });
        }).start();
    }

    // Este método agora recebe o DTO de detalhes
    private void popularCampos(DetalhesChamadoDto chamadoDto) {
        tituloLabel.setText(chamadoDto.getTitulo());
        statusComboBox.setValue(chamadoDto.getStatus()); // Assumindo que o DTO de detalhes também tem o status

        // --- CORREÇÃO 4: A lista de mensagens já vem no formato DTO ---
        mensagensListView.setItems(FXCollections.observableArrayList(chamadoDto.getMensagens()));
        mensagensListView.scrollTo(mensagensListView.getItems().size() - 1);

        // Atualiza o label do SLA
        atualizarLabelSla(chamadoDto);
    }

    // (O resto da classe continua igual, com os métodos handleEnviarMensagem, handleSalvarStatus, etc.)

    @FXML
    private void handleEnviarMensagem() {
        String textoResposta = respostaArea.getText();
        if (textoResposta == null || textoResposta.isBlank()) {
            mostrarAlerta("Atenção", "A mensagem não pode estar vazia.");
            return;
        }

        webSocketService.sendMessage(textoResposta, this.chamadoId);
        respostaArea.clear();
    }

    @FXML
    private void handleSalvarStatus() {
        StatusChamado novoStatus = statusComboBox.getValue();
        if (novoStatus == null) {
            mostrarAlerta("Erro", "Por favor, selecione um status.");
            return;
        }

        StatusUpdateDto dto = new StatusUpdateDto();
        dto.setNovoStatus(novoStatus);

        salvarStatusButton.setDisable(true);
        salvarStatusButton.setText("Salvando...");

        new Thread(() -> {
            String token = LoginController.authToken;
            boolean sucesso = apiService.atualizarStatus(this.chamadoId, dto, token);

            Platform.runLater(() -> {
                if (sucesso) {
                    mostrarAlerta("Sucesso", "Status do chamado atualizado com sucesso!");
                } else {
                    mostrarAlerta("Erro", "Não foi possível atualizar o status.");
                }
                salvarStatusButton.setDisable(false);
                salvarStatusButton.setText("Salvar Status");
            });
        }).start();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void atualizarLabelSla(DetalhesChamadoDto dto) {
        LocalDateTime prazo = dto.getDataLimiteResolucao();
        if (prazo == null) {
            labelSlaStatus.setText("SLA não aplicável para este chamado.");
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        String statusSla = dto.getStatusSla();

        if ("VIOLADO".equals(statusSla) || agora.isAfter(prazo)) {
            labelSlaStatus.setText("Prazo de resolução violado.");
            labelSlaStatus.setTextFill(Color.RED);
        } else {
            Duration duracao = Duration.between(agora, prazo);
            long dias = duracao.toDays();
            long horas = duracao.toHours() % 24;
            long minutos = duracao.toMinutes() % 60;

            String texto = String.format("Tempo restante: %d dias, %dh e %dm", dias, horas, minutos);
            labelSlaStatus.setText(texto);

            if ("PROXIMO_VENCIMENTO".equals(statusSla)) {
                labelSlaStatus.setTextFill(Color.ORANGE);
            } else {
                labelSlaStatus.setTextFill(Color.BLACK);
            }
        }
    }
}