package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChatMessageDto;
import br.com.applogin.applogin.dto.MensagemDto;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.Mensagem;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MensagemRepository mensagemRepository;
    @Autowired
    private ChamadoRepository chamadoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {
        // Busca o usuário que enviou a mensagem (já autenticado pelo Spring Security)
        Usuario autor = usuarioRepository.findByEmail(principal.getName());

        // Busca o chamado ao qual a mensagem pertence
        Chamado chamado = chamadoRepository.findById(chatMessageDto.getChamadoId()).orElse(null);

        if (autor != null && chamado != null) {
            // Cria e salva a entidade Mensagem no banco de dados
            Mensagem mensagem = new Mensagem();
            mensagem.setTexto(chatMessageDto.getTexto());
            mensagem.setChamado(chamado);
            mensagem.setAutor(autor);
            mensagem.setDataEnvio(LocalDateTime.now());
            mensagemRepository.save(mensagem);

            // Converte a mensagem salva para um DTO de resposta
            MensagemDto mensagemDeResposta = new MensagemDto(mensagem);

            // Envia (retransmite) a mensagem para todos que estão inscritos no tópico daquele chamado
            messagingTemplate.convertAndSend("/topic/chamados/" + chamado.getId(), mensagemDeResposta);
        }
    }
}