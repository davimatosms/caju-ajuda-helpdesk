package br.com.applogin.applogin.service;

import br.com.applogin.applogin.dto.ChatMessageDto;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.Mensagem;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class ChatService {

    @Autowired
    private MensagemRepository mensagemRepository;
    @Autowired
    private ChamadoRepository chamadoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EmailService emailService;

    // A transação agora engloba toda a operação
    @Transactional
    public Mensagem processarEEnviarMensagem(ChatMessageDto chatMessageDto, Principal principal) {
        Usuario autor = usuarioRepository.findByEmail(principal.getName());
        // Usamos orElseThrow para garantir que o chamado existe
        Chamado chamado = chamadoRepository.findById(chatMessageDto.getChamadoId())
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));

        Mensagem mensagem = new Mensagem();
        mensagem.setTexto(chatMessageDto.getTexto());
        mensagem.setChamado(chamado);
        mensagem.setAutor(autor);
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagemRepository.save(mensagem);

        // Se o autor for técnico, a notificação por e-mail é enviada DENTRO da mesma transação
        if (autor.getRole() == UsuarioRole.TECNICO && chamado.getCliente() != null) {
            emailService.enviarNotificacaoDeNovaMensagem(mensagem);
        }

        return mensagem;
    }
}