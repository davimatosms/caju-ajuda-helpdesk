package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChamadoDetalhesDto; // Nova importação
import br.com.applogin.applogin.dto.ChamadoDto;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.repository.ChamadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tecnico")
public class TecnicoApiController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @GetMapping("/chamados")
    public List<ChamadoDto> listarTodosOsChamados() {
        List<Chamado> chamados = (List<Chamado>) chamadoRepository.findAll();
        return chamados.stream()
                .map(ChamadoDto::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/chamados/{id}")
    public ResponseEntity<ChamadoDetalhesDto> obterChamadoPorId(@PathVariable Long id) {
        return chamadoRepository.findById(id)
                .map(chamado -> new ChamadoDetalhesDto(chamado)) // Converte a entidade para DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}