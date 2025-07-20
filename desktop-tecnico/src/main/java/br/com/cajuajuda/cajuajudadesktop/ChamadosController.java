package br.com.cajuajuda.cajuajudadesktop;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ChamadosController {

    // ===================================================================
    // DECLARAÇÕES @FXML QUE ESTAVAM FALTANDO
    // ===================================================================
    @FXML
    private TableView<ChamadoDto> chamadosTable;
    @FXML
    private TableColumn<ChamadoDto, Long> idColumn;
    @FXML
    private TableColumn<ChamadoDto, String> tituloColumn;
    @FXML
    private TableColumn<ChamadoDto, String> clienteColumn;
    @FXML
    private TableColumn<ChamadoDto, StatusChamado> statusColumn;
    @FXML
    private TableColumn<ChamadoDto, PrioridadeChamado> prioridadeColumn;
    @FXML
    private TableColumn<ChamadoDto, LocalDateTime> dataColumn;
    // ===================================================================

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        configurarColunas();
        configurarListenerDeSelecao();
        carregarChamados();
    }

    private void configurarColunas() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tituloColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        prioridadeColumn.setCellValueFactory(new PropertyValueFactory<>("prioridade"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
    }

    private void configurarListenerDeSelecao() {
        chamadosTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                ChamadoDto chamadoSelecionado = chamadosTable.getSelectionModel().getSelectedItem();
                if (chamadoSelecionado != null) {
                    abrirTelaDeDetalhes(chamadoSelecionado);
                }
            }
        });
    }

    private void abrirTelaDeDetalhes(ChamadoDto chamado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("detalhes-chamado-view.fxml"));
            Scene scene = new Scene(loader.load());

            DetalhesChamadoController controller = loader.getController();
            controller.carregarDadosIniciais(chamado.getId());

            Stage stage = new Stage();
            stage.setTitle("Detalhes do Chamado #" + chamado.getId());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Após fechar a janela de detalhes, atualiza a lista principal
            carregarChamados();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarChamados() {
        String token = LoginController.authToken;
        if (token == null) {
            System.err.println("Erro: Token de autenticação não encontrado.");
            return;
        }
        new Thread(() -> {
            List<ChamadoDto> chamados = apiService.getTodosChamados(token);
            Platform.runLater(() -> {
                chamadosTable.setItems(FXCollections.observableArrayList(chamados));
            });
        }).start();
    }
}