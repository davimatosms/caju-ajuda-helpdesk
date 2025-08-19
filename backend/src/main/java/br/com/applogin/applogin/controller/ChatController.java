package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChatMessageDto;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Este método agora será o ponto de entrada para novas mensagens enviadas pelo cliente.
     * Ele é acionado quando um cliente envia uma mensagem para o destino "/app/chat.sendMessage".
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {
        // O 'principal' é o usuário autenticado na conexão WebSocket, garantido pelo nosso JwtAuthenticationFilter.
        // Verificamos se ele não é nulo por segurança.
        if (principal != null && principal.getName() != null) {
            Usuario autor = usuarioRepository.findByEmail(principal.getName());
            if (autor != null) {
                // Usamos o mesmo serviço que já salva e faz o broadcast da mensagem.
                chatService.processarNovaMensagem(chatMessageDto.getChamadoId(), chatMessageDto.getTexto(), autor);
            } else {
                System.err.println("ERRO: Usuário não encontrado no banco de dados: " + principal.getName());
            }
        } else {
            System.err.println("ERRO: Tentativa de envio de mensagem sem autenticação no Principal do WebSocket.");
        }
    }
}