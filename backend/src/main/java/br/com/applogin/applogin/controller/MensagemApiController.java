package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.Mensagem;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chamados/{chamadoId}/mensagens")
public class MensagemApiController {

    @Autowired
    private MensagemRepository mensagemRepository;
    @Autowired
    private ChamadoRepository chamadoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<Mensagem> enviarMensagem(
            @PathVariable Long chamadoId,
            @RequestBody Map<String, String> payload) {

        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario autor = usuarioRepository.findByEmail(emailUsuarioLogado);

        Optional<Chamado> chamadoOpt = chamadoRepository.findById(chamadoId);
        if (chamadoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Chamado chamado = chamadoOpt.get();


        boolean isClienteDoChamado = chamado.getCliente().equals(autor);
        boolean isTecnico = autor.getRole().name().equals("TECNICO");

        if (!isClienteDoChamado && !isTecnico) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setTexto(payload.get("texto"));
        novaMensagem.setAutor(autor);
        novaMensagem.setChamado(chamado);
        novaMensagem.setDataEnvio(LocalDateTime.now());

        Mensagem mensagemSalva = mensagemRepository.save(novaMensagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensagemSalva);
    }
}