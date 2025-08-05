package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChamadoDetalhesDto;
import br.com.applogin.applogin.dto.ChamadoDto;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cliente")
public class ClienteApiController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/chamados")
    public ResponseEntity<List<ChamadoDto>> getMeusChamadosAbertos(Authentication authentication) {
        Usuario cliente = usuarioRepository.findByEmail(authentication.getName());
        List<Chamado> chamados = chamadoRepository.findByClienteAndStatusNotIn(cliente, List.of(StatusChamado.FECHADO));
        List<ChamadoDto> chamadosDto = chamados.stream()
                .map(ChamadoDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chamadosDto);
    }

    @GetMapping("/chamados/{id}")
    public ResponseEntity<ChamadoDetalhesDto> getDetalhesDoChamado(@PathVariable Long id, Authentication authentication) {
        Usuario cliente = usuarioRepository.findByEmail(authentication.getName());

        Chamado chamado = chamadoRepository.findById(id)
                .filter(c -> c.getCliente().getId() == cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chamado não encontrado ou não pertence a este usuário."));

        return ResponseEntity.ok(new ChamadoDetalhesDto(chamado));
    }
}