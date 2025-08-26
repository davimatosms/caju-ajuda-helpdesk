package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChamadoDto;
import br.com.applogin.applogin.dto.DetalhesChamadoDto;
import br.com.applogin.applogin.dto.StatusUpdateDto;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.GeminiService; // Importe o GeminiService
import br.com.applogin.applogin.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Importe o Map
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

    // --- NOVA INJEÇÃO DE DEPENDÊNCIA PARA A IA ---
    @Autowired
    private GeminiService geminiService;

    @GetMapping("/chamados")
    public List<ChamadoDto> getTodosChamados() {
        return chamadoRepository.findAll().stream()
                .map(ChamadoDto::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/chamados/{id}")
    public ResponseEntity<DetalhesChamadoDto> getDetalhesChamado(@PathVariable Long id) {
        return chamadoRepository.findById(id)
                .map(chamado -> ResponseEntity.ok(new DetalhesChamadoDto(chamado)))
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

    // --- NOVO ENDPOINT PARA A IA ---
    @GetMapping("/chamados/{id}/sugerir-resposta")
    public ResponseEntity<Map<String, String>> sugerirResposta(@PathVariable Long id) {
        try {
            String sugestao = geminiService.gerarSugestaoDeResposta(id);
            return ResponseEntity.ok(Map.of("sugestao", sugestao));
        } catch (Exception e) {
            e.printStackTrace(); // Para vermos o erro no console
            return ResponseEntity.badRequest().body(Map.of("erro", "Não foi possível gerar a sugestão."));
        }
    }
}