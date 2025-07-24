package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChatMessageDto;
import br.com.applogin.applogin.dto.MensagemDto;
import br.com.applogin.applogin.model.Mensagem;
import br.com.applogin.applogin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatService chatService; // Injeta o novo serviço

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {
        try {
            // Delega toda a lógica de negócio para o ChatService
            Mensagem mensagemSalva = chatService.processarEEnviarMensagem(chatMessageDto, principal);

            // Envia a mensagem via WebSocket para o tópico do chamado
            MensagemDto mensagemDeResposta = new MensagemDto(mensagemSalva);
            messagingTemplate.convertAndSend("/topic/chamados/" + mensagemSalva.getChamado().getId(), mensagemDeResposta);

        } catch (Exception e) {
            // É uma boa prática logar o erro aqui
            System.err.println("Erro ao processar mensagem: " + e.getMessage());
            // Opcional: notificar o usuário sobre o erro via WebSocket
        }
    }
}