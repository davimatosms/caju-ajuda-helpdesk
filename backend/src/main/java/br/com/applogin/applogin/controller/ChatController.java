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

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {

        Usuario autor = usuarioRepository.findByEmail(principal.getName());

        if (autor != null) {
            // Chama o método correto no ChatService, passando o objeto Usuario
            chatService.processarMensagemDoWebSocket(chatMessageDto, autor);
        }
    }
}