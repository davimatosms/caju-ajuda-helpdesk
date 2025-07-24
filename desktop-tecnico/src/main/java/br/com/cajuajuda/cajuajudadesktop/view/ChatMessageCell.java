package br.com.cajuajuda.cajuajudadesktop.view;

import br.com.cajuajuda.cajuajudadesktop.MainApp;
import br.com.cajuajuda.cajuajudadesktop.dto.MensagemDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ChatMessageCell extends ListCell<MensagemDto> {

    @FXML
    private HBox messageContainer;
    @FXML
    private VBox messageBubble;
    @FXML
    private Label authorLabel;
    @FXML
    private Label messageTextLabel;
    @FXML
    private Label timestampLabel;

    private FXMLLoader fxmlLoader;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void updateItem(MensagemDto mensagem, boolean empty) {
        super.updateItem(mensagem, empty);

        if (empty || mensagem == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Carrega o FXML apenas uma vez por célula para otimizar a performance
            if (fxmlLoader == null) {
                // A CORREÇÃO PRINCIPAL ESTÁ AQUI: Usando MainApp para o caminho absoluto
                fxmlLoader = new FXMLLoader(MainApp.class.getResource("chat-message-cell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Popula os labels com os dados da mensagem
            authorLabel.setText(mensagem.getAutor().getNome());
            messageTextLabel.setText(mensagem.getTexto());
            if (mensagem.getDataEnvio() != null) {
                timestampLabel.setText(mensagem.getDataEnvio().format(formatter));
            } else {
                timestampLabel.setText("");
            }

            // Lógica para alinhar a "bolha" da mensagem e mudar o estilo
            String userRole = mensagem.getAutor().getRole();
            if ("TECNICO".equals(userRole)) {
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                messageBubble.getStyleClass().setAll("message-bubble", "tecnico-bubble");
            } else {
                messageContainer.setAlignment(Pos.CENTER_LEFT);
                messageBubble.getStyleClass().setAll("message-bubble", "cliente-bubble");
            }

            // Define o conteúdo gráfico da célula
            setGraphic(messageContainer);
        }
    }
}