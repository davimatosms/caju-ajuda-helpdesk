package br.com.applogin.applogin.service;

import br.com.applogin.applogin.dto.ChatMessageDto;
import br.com.applogin.applogin.dto.MensagemDto;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.Mensagem;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class ChatService {

    @Autowired private MensagemRepository mensagemRepository;
    @Autowired private ChamadoRepository chamadoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmailService emailService;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void processarMensagemDoWebSocket(ChatMessageDto chatMessageDto, Usuario autor) {
        Chamado chamado = chamadoRepository.findById(chatMessageDto.getChamadoId())
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));

        Mensagem mensagem = new Mensagem();
        mensagem.setTexto(chatMessageDto.getTexto());
        mensagem.setChamado(chamado);
        mensagem.setAutor(autor);
        mensagem.setDataEnvio(LocalDateTime.now());
        Mensagem mensagemSalva = mensagemRepository.save(mensagem);

        // Notifica o WebSocket com a nova mensagem
        messagingTemplate.convertAndSend("/topic/chamados/" + chamado.getId(), new MensagemDto(mensagemSalva));

        // Envia notificação por e-mail se a mensagem for de um técnico
        if (autor.getRole() == UsuarioRole.TECNICO) {
            emailService.enviarNotificacaoDeNovaMensagem(mensagemSalva);
        }
    }

    @Transactional
    public void processarNovaMensagem(Long chamadoId, String texto, Usuario autor) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado com ID: " + chamadoId));

        Mensagem mensagem = new Mensagem();
        mensagem.setTexto(texto);
        mensagem.setChamado(chamado);
        mensagem.setAutor(autor);
        mensagem.setDataEnvio(LocalDateTime.now());
        Mensagem mensagemSalva = mensagemRepository.save(mensagem);

        // A linha crucial: envia a mensagem salva para o WebSocket
        messagingTemplate.convertAndSend("/topic/chamados/" + chamadoId, new MensagemDto(mensagemSalva));
    }
}