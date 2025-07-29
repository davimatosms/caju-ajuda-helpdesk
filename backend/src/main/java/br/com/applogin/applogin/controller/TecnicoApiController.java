package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChamadoDto;
import br.com.applogin.applogin.dto.DetalhesChamadoDto; // Corrigido para usar o DTO do backend
import br.com.applogin.applogin.dto.StatusUpdateDto;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tecnico")
public class TecnicoApiController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TecnicoService tecnicoService;

    @GetMapping("/chamados")
    public List<ChamadoDto> getTodosChamados() {
        return chamadoRepository.findAll().stream()
                .map(ChamadoDto::new) // Usa o DTO do backend
                .collect(Collectors.toList());
    }


    @GetMapping("/chamados/{id}")
    public ResponseEntity<DetalhesChamadoDto> getDetalhesChamado(@PathVariable Long id) {
        return chamadoRepository.findById(id)
                .map(chamado -> ResponseEntity.ok(new DetalhesChamadoDto(chamado))) // Usa o DTO do backend
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/chamados/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id,
                                                @RequestBody StatusUpdateDto statusUpdateDto,
                                                Authentication authentication) {
        try {
            Usuario tecnico = usuarioRepository.findByEmail(authentication.getName());
            tecnicoService.atualizarStatusChamado(id, statusUpdateDto.getNovoStatus(), tecnico);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}