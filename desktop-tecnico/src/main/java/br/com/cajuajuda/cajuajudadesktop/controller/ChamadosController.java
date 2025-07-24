package br.com.cajuajuda.cajuajudadesktop.controller;

import br.com.cajuajuda.cajuajudadesktop.MainApp;
import br.com.cajuajuda.cajuajudadesktop.dto.ChamadoDto;
import br.com.cajuajuda.cajuajudadesktop.dto.PrioridadeChamado;
import br.com.cajuajuda.cajuajudadesktop.dto.StatusChamado;
import br.com.cajuajuda.cajuajudadesktop.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ChamadosController {

    @FXML private TableView<ChamadoDto> chamadosTable;
    @FXML private TableColumn<ChamadoDto, String> slaColumn;
    @FXML private TableColumn<ChamadoDto, Long> idColumn;
    @FXML private TableColumn<ChamadoDto, String> tituloColumn;
    @FXML private TableColumn<ChamadoDto, String> clienteColumn;
    @FXML private TableColumn<ChamadoDto, StatusChamado> statusColumn;
    @FXML private TableColumn<ChamadoDto, PrioridadeChamado> prioridadeColumn;
    @FXML private TableColumn<ChamadoDto, LocalDateTime> dataColumn;

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        configurarColunas();
        configurarListenerDeSelecao();
        carregarChamados();
    }

    private void configurarColunas() {
        // Configuração da coluna de SLA
        slaColumn.setCellValueFactory(new PropertyValueFactory<>("statusSla"));
        slaColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Circle circle = new Circle(8);
                    Tooltip tooltip = new Tooltip();
                    switch (item) {
                        case "VIOLADO" -> {
                            circle.setFill(Color.RED);
                            tooltip.setText("SLA Violado");
                        }
                        case "PROXIMO_VENCIMENTO" -> {
                            circle.setFill(Color.ORANGE);
                            tooltip.setText("Próximo do Vencimento");
                        }
                        default -> { // NO_PRAZO
                            circle.setFill(Color.LIMEGREEN);
                            tooltip.setText("No Prazo");
                        }
                    }
                    setGraphic(circle);
                    Tooltip.install(this, tooltip);
                }
            }
        });

        // Configuração das outras colunas
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
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("detalhes-chamado-view.fxml"));
            Scene scene = new Scene(loader.load());

            // --- ESTA É A CORREÇÃO ---
            // Usamos MainApp.class para garantir que o caminho para o CSS seja encontrado
            String css = MainApp.class.getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            DetalhesChamadoController controller = loader.getController();
            controller.carregarDadosIniciais(chamado.getId());

            Stage stage = new Stage();
            stage.setTitle("Detalhes do Chamado #" + chamado.getId());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarChamados();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarChamados() {
        String token = LoginController.authToken;
        if (token == null) { return; }
        new Thread(() -> {
            List<ChamadoDto> chamados = apiService.getTodosChamados(token);
            Platform.runLater(() -> {
                chamadosTable.setItems(FXCollections.observableArrayList(chamados));
            });
        }).start();
    }
}