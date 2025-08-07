package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChamadoDetalhesDto;
import br.com.applogin.applogin.dto.ChamadoDto;
import br.com.applogin.applogin.dto.NovaMensagemDto;
import br.com.applogin.applogin.dto.NovoChamadoDto;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.ChamadoService;
import br.com.applogin.applogin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cliente")
public class ClienteApiController {

    @Autowired private ChamadoRepository chamadoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ChamadoService chamadoService;
    @Autowired private ChatService chatService;

    @GetMapping("/chamados")
    public ResponseEntity<List<ChamadoDto>> getMeusChamadosAbertos(Authentication authentication) {
        Usuario cliente = usuarioRepository.findByEmail(authentication.getName());
        List<Chamado> chamados = chamadoRepository.findByClienteAndStatusNotIn(cliente, List.of(StatusChamado.FECHADO));
        return ResponseEntity.ok(chamados.stream().map(ChamadoDto::new).collect(Collectors.toList()));
    }

    @GetMapping("/chamados/{id}")
    public ResponseEntity<ChamadoDetalhesDto> getDetalhesDoChamado(@PathVariable Long id, Authentication authentication) {
        Usuario cliente = usuarioRepository.findByEmail(authentication.getName());
        Chamado chamado = chamadoRepository.findById(id)
                .filter(c -> c.getCliente().getId() == cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(new ChamadoDetalhesDto(chamado));
    }

    @PostMapping("/chamados")
    public ResponseEntity<ChamadoDto> criarNovoChamado(@RequestBody NovoChamadoDto novoChamadoDto, Authentication authentication) {
        Usuario cliente = usuarioRepository.findByEmail(authentication.getName());
        Chamado chamadoSalvo = chamadoService.criarNovoChamadoPeloCliente(novoChamadoDto, cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ChamadoDto(chamadoSalvo));
    }

    @PostMapping("/chamados/{id}/mensagens")
    public ResponseEntity<Void> enviarMensagem(@PathVariable Long id, @RequestBody NovaMensagemDto novaMensagemDto, Authentication authentication) {
        Usuario cliente = usuarioRepository.findByEmail(authentication.getName());
        chatService.processarNovaMensagem(id, novaMensagemDto.getTexto(), cliente);
        return ResponseEntity.ok().build();
    }
}