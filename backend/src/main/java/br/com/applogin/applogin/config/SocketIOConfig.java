package br.com.applogin.applogin.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0"); // Escuta em todos os IPs disponíveis na máquina
        config.setPort(9092); // Porta dedicada para o Socket.IO

        final SocketIOServer server = new SocketIOServer(config);

        // Adiciona um listener para o evento de conexão
        server.addConnectListener(client -> {
            System.out.println("--- [Socket.IO] Cliente conectado: " + client.getSessionId());
        });

        // Adiciona um listener para o evento de desconexão
        server.addDisconnectListener(client -> {
            System.out.println("--- [Socket.IO] Cliente desconectado: " + client.getSessionId());
        });

        // Adiciona um listener para o evento 'joinRoom' que o cliente vai enviar
        server.addEventListener("joinRoom", String.class, (client, roomName, ackSender) -> {
            client.joinRoom(roomName);
            System.out.println("--- [Socket.IO] Cliente " + client.getSessionId() + " entrou na sala: " + roomName);
        });

        server.start();
        System.out.println("--- Servidor Socket.IO iniciado com sucesso na porta 9092 ---");
        return server;
    }
}