package br.com.applogin.applogin.service;

import br.com.applogin.applogin.dto.MensagemDto;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.Mensagem;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository;
import com.corundumstudio.socketio.SocketIOServer; // Importe a nova classe
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Service
public class ChatService {

    @Autowired private MensagemRepository mensagemRepository;
    @Autowired private ChamadoRepository chamadoRepository;
    @Autowired private EmailService emailService;

    // --- MUDANÇA: Trocamos o SimpMessagingTemplate pelo SocketIOServer ---
    @Autowired
    private SocketIOServer socketServer;

    @Transactional
    public void processarNovaMensagem(Long chamadoId, String textoMensagem, Usuario autor) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chamado não encontrado"));

        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setTexto(textoMensagem);
        novaMensagem.setAutor(autor);
        novaMensagem.setChamado(chamado);
        novaMensagem.setDataEnvio(LocalDateTime.now());

        Mensagem mensagemSalva = mensagemRepository.save(novaMensagem);

        // --- MUDANÇA: Usamos o novo servidor para enviar a mensagem ---
        // O "evento" se chama 'nova_mensagem_chamado_{id}' e enviamos o DTO.
        String roomName = "chamado_" + chamadoId;
        socketServer.getRoomOperations(roomName).sendEvent("nova_mensagem", new MensagemDto(mensagemSalva));

        System.out.println("--- [ChatService] Mensagem ID " + mensagemSalva.getId() + " enviada via Socket.IO para a sala: " + roomName);

        if (autor.getRole() == UsuarioRole.TECNICO) {
            emailService.enviarNotificacaoDeNovaMensagem(mensagemSalva);
        }
    }
}