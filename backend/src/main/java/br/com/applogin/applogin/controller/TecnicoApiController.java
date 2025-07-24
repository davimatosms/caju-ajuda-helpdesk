package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChamadoDetalhesDto;
import br.com.applogin.applogin.dto.ChamadoDto;
import br.com.applogin.applogin.dto.MensagemDto;
import br.com.applogin.applogin.dto.StatusUpdateDto; // Nova importação
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.Mensagem; // Nova importação
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository; // Nova importação
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate; // Nova importação
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // Nova importação
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tecnico")
public class TecnicoApiController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    // --- NOVAS DEPENDÊNCIAS ---
    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
                .map(ChamadoDetalhesDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===================================================================
    // NOVO ENDPOINT PARA ATUALIZAR O STATUS
    // ===================================================================
    @PutMapping("/chamados/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateDto statusUpdateDto) {

        return chamadoRepository.findById(id).map(chamado -> {
            // 1. Atualiza o status do chamado
            chamado.setStatus(statusUpdateDto.getNovoStatus());
            chamadoRepository.save(chamado);

            // 2. Cria uma mensagem de sistema para registrar a alteração no chat
            Mensagem msgSistema = new Mensagem();
            msgSistema.setChamado(chamado);
            msgSistema.setAutor(chamado.getCliente()); // Pode ser o cliente ou um usuário "Sistema"
            msgSistema.setDataEnvio(LocalDateTime.now());
            msgSistema.setTexto("--- O status do chamado foi alterado para: " + statusUpdateDto.getNovoStatus() + " ---");

            Mensagem mensagemSalva = mensagemRepository.save(msgSistema);

            // 3. Envia a nova mensagem para o WebSocket para que todos vejam em tempo real
            messagingTemplate.convertAndSend("/topic/chamados/" + id, new MensagemDto(mensagemSalva));

            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}