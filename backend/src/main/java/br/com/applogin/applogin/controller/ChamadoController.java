package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChamadoDetalhesDto;
import br.com.applogin.applogin.model.*;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.ChamadoService;
import br.com.applogin.applogin.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/chamados")
public class ChamadoController {

    @Autowired private ChamadoRepository chamadoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ChamadoService chamadoService;
    @Autowired private FileStorageService fileStorageService;

    // ... (os outros métodos permanecem iguais) ...
    @GetMapping("/meus")
    public String listarMeusChamados(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cliente = usuarioRepository.findByEmail(email);
        List<Chamado> chamados = chamadoRepository.findByClienteAndStatusNotIn(cliente, List.of(StatusChamado.FECHADO));
        model.addAttribute("chamados", chamados);
        return "meus-chamados";
    }

    @GetMapping("/fechados")
    public String listarChamadosFechados(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cliente = usuarioRepository.findByEmail(email);
        List<Chamado> chamados = chamadoRepository.findByClienteAndStatusIn(cliente, List.of(StatusChamado.FECHADO));
        model.addAttribute("chamados", chamados);
        return "chamados-fechados";
    }

    @GetMapping("/novo")
    public String mostrarFormularioNovoChamado(Model model) {
        model.addAttribute("chamado", new Chamado());
        model.addAttribute("prioridades", PrioridadeChamado.values());
        return "novo-chamado";
    }

    @PostMapping
    public String salvarNovoChamado(@Valid @ModelAttribute("chamado") Chamado chamado,
                                    BindingResult result,
                                    @RequestParam("anexosFile") List<MultipartFile> anexosFile,
                                    RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.chamado", result);
            redirectAttributes.addFlashAttribute("chamado", chamado);
            return "redirect:/chamados/novo";
        }
        try {
            String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario cliente = usuarioRepository.findByEmail(emailUsuarioLogado);
            chamado.setCliente(cliente);

            Mensagem primeiraMensagem = new Mensagem();
            primeiraMensagem.setTexto(chamado.getDescricao());
            primeiraMensagem.setAutor(cliente);
            primeiraMensagem.setChamado(chamado);
            primeiraMensagem.setDataEnvio(LocalDateTime.now());

            for (MultipartFile file : anexosFile) {
                if (file != null && !file.isEmpty()) {
                    String nomeUnico = fileStorageService.storeFile(file);
                    Anexo anexo = new Anexo();
                    anexo.setNomeArquivo(file.getOriginalFilename());
                    anexo.setTipoArquivo(file.getContentType());
                    anexo.setNomeUnico(nomeUnico);

                    // --- CORREÇÃO AQUI: Estabelece a ligação bidirecional ---
                    anexo.setMensagem(primeiraMensagem);

                    primeiraMensagem.getAnexos().add(anexo);
                }
            }
            chamado.getMensagens().add(primeiraMensagem);
            Chamado chamadoSalvo = chamadoService.criarNovoChamado(chamado);
            redirectAttributes.addFlashAttribute("success_message", "Chamado #" + chamadoSalvo.getId() + " aberto com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_message", "Erro ao abrir o chamado: " + e.getMessage());
            e.printStackTrace(); // Para vermos o erro completo no log
        }
        return "redirect:/chamados/meus";
    }

    @GetMapping("/{id:[0-9]+}")
    public String exibirDetalhesChamado(@PathVariable("id") Long id, Model model) {
        Optional<Chamado> chamadoOpt = chamadoRepository.findById(id);
        if (chamadoOpt.isEmpty()) {
            return "redirect:/chamados/meus?error=not_found";
        }
        Chamado chamado = chamadoOpt.get();
        model.addAttribute("chamado", new ChamadoDetalhesDto(chamado));
        return "detalhes-chamado";
    }

    @GetMapping("/anexos/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadAnexo(@PathVariable String filename) {
        Resource file = fileStorageService.loadFileAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}