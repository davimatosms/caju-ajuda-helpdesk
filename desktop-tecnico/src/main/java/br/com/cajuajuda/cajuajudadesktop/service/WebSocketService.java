package br.com.cajuajuda.cajuajudadesktop.service;

import br.com.cajuajuda.cajuajudadesktop.dto.ChatMessageDto;
import br.com.cajuajuda.cajuajudadesktop.dto.MensagemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders; // Nova importação
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class WebSocketService {

    private final WebSocketStompClient stompClient;
    private StompSession stompSession;

    public WebSocketService() {
        // Configura o Jackson para entender datas
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);

        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(converter);
    }

    // MÉTODO DE CONEXÃO CORRIGIDO E FINAL
    public void connect(String token, Long chamadoId, Consumer<MensagemDto> onMessageReceived) {
        String URL = "ws://localhost:8080/ws-chat-java";

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        // Criamos um objeto vazio para o parâmetro que estava causando a ambiguidade
        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        try {
            // A chamada agora é explícita e sem ambiguidade
            stompSession = stompClient.connectAsync(URL, handshakeHeaders, connectHeaders, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("Conectado e AUTENTICADO ao WebSocket!");
                    session.subscribe("/topic/chamados/" + chamadoId, this);
                }
                @Override
                public Type getPayloadType(StompHeaders headers) { return MensagemDto.class; }
                @Override
                public void handleFrame(StompHeaders headers, Object payload) { onMessageReceived.accept((MensagemDto) payload); }
                @Override
                public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) { ex.printStackTrace(); }
                @Override
                public void handleTransportError(StompSession session, Throwable exception) { exception.printStackTrace(); }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String texto, Long chamadoId) {
        if (stompSession != null && stompSession.isConnected()) {
            ChatMessageDto chatMessage = new ChatMessageDto();
            chatMessage.setTexto(texto);
            chatMessage.setChamadoId(chamadoId);
            stompSession.send("/app/chat.sendMessage", chatMessage);
        }
    }

    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }
}