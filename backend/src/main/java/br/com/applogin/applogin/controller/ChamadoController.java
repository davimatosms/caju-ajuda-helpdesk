package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.ChamadoDetalhesDto;
import br.com.applogin.applogin.model.*;
import br.com.applogin.applogin.repository.AnexoRepository;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.MensagemRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/chamados")
public class ChamadoController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AnexoRepository anexoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MensagemRepository mensagemRepository;

    @GetMapping("/meus")
    public String listarMeusChamados(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cliente = usuarioRepository.findByEmail(email);
        List<Chamado> chamados = chamadoRepository.findByCliente(cliente);
        model.addAttribute("chamados", chamados);
        return "meus-chamados";
    }

    @GetMapping("/{id}")
    public String verDetalhesDoChamado(@PathVariable("id") Long id, Model model) {
        Optional<Chamado> chamadoOpt = chamadoRepository.findById(id);
        if (chamadoOpt.isEmpty()) {
            return "redirect:/chamados/meus";
        }

        Chamado chamado = chamadoOpt.get();
        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!chamado.getCliente().getEmail().equals(emailUsuarioLogado)) {
            return "redirect:/chamados/meus?error=acesso_negado";
        }

        // CORREÇÃO FINAL: Converte a entidade para DTO antes de enviar para a view
        // Isso evita qualquer erro de serialização ou lazy loading no Thymeleaf.
        model.addAttribute("chamado", new ChamadoDetalhesDto(chamado));

        return "detalhes-chamado";
    }

    @GetMapping("/novo")
    public String mostrarFormularioNovoChamado(Model model) {
        model.addAttribute("chamado", new Chamado());
        model.addAttribute("prioridades", PrioridadeChamado.values());
        return "novo-chamado";
    }

    @PostMapping
    public String salvarNovoChamado(@ModelAttribute Chamado chamado, @RequestParam("anexosFile") MultipartFile[] anexosFile, RedirectAttributes redirectAttributes) {
        try {
            String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario cliente = usuarioRepository.findByEmail(emailUsuarioLogado);
            chamado.setCliente(cliente);
            chamado.setDataCriacao(LocalDateTime.now());
            chamado.setStatus(StatusChamado.ABERTO);
            Chamado chamadoSalvo = chamadoRepository.save(chamado);

            for (MultipartFile file : anexosFile) {
                if (file != null && !file.isEmpty()) {
                    String nomeUnico = fileStorageService.storeFile(file);
                    Anexo anexo = new Anexo();
                    anexo.setNomeArquivo(file.getOriginalFilename());
                    anexo.setTipoArquivo(file.getContentType());
                    anexo.setNomeUnico(nomeUnico);
                    anexo.setChamado(chamadoSalvo);
                    anexoRepository.save(anexo);
                }
            }
            redirectAttributes.addFlashAttribute("success_message", "Chamado #" + chamadoSalvo.getId() + " aberto com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_message", "Erro ao abrir o chamado: " + e.getMessage());
        }
        return "redirect:/chamados/meus";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable("id") Long id, Model model) {
        Optional<Chamado> chamadoOpt = chamadoRepository.findById(id);
        if (chamadoOpt.isEmpty()) { return "redirect:/chamados/meus"; }

        Chamado chamado = chamadoOpt.get();
        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!chamado.getCliente().getEmail().equals(emailUsuarioLogado)) {
            return "redirect:/chamados/meus?error=acesso_negado";
        }
        model.addAttribute("chamado", chamado);
        model.addAttribute("prioridades", PrioridadeChamado.values());
        return "editar-chamado";
    }

    @PostMapping("/{id}/editar")
    public String salvarEdicaoChamado(@PathVariable("id") Long id,
                                      @ModelAttribute("chamado") Chamado chamadoAtualizado,
                                      RedirectAttributes redirectAttributes) {

        Optional<Chamado> chamadoOpt = chamadoRepository.findById(id);
        if (chamadoOpt.isEmpty()) {
            return "redirect:/chamados/meus";
        }

        Chamado chamadoOriginal = chamadoOpt.get();
        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!chamadoOriginal.getCliente().getEmail().equals(emailUsuarioLogado)) {
            return "redirect:/chamados/meus?error=acesso_negado";
        }

        // Apenas o título e prioridade podem ser editados. A descrição virou um histórico.
        chamadoOriginal.setTitulo(chamadoAtualizado.getTitulo());
        chamadoOriginal.setPrioridade(chamadoAtualizado.getPrioridade());

        chamadoRepository.save(chamadoOriginal);

        redirectAttributes.addFlashAttribute("success_message", "Chamado #" + id + " atualizado com sucesso!");
        return "redirect:/chamados/" + id;
    }

    @GetMapping("/anexos/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadAnexo(@PathVariable String filename) {
        Resource file = fileStorageService.loadFileAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    /**
     * Método unificado que permite ao cliente enviar uma mensagem de texto,
     * um ou mais arquivos, ou ambos ao mesmo tempo.
     */
    @PostMapping("/{id}/enviar-mensagem")
    public String enviarMensagemPeloCliente(
            @PathVariable("id") Long id,
            @RequestParam(value = "texto", required = false) String texto,
            @RequestParam("anexosFile") MultipartFile[] anexosFile,
            RedirectAttributes redirectAttributes) {

        Optional<Chamado> chamadoOpt = chamadoRepository.findById(id);
        if (chamadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error_message", "Chamado não encontrado.");
            return "redirect:/chamados/meus";
        }
        Chamado chamado = chamadoOpt.get();

        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario autor = usuarioRepository.findByEmail(emailUsuarioLogado);

        if (!chamado.getCliente().equals(autor)) {
            redirectAttributes.addFlashAttribute("error_message", "Acesso negado.");
            return "redirect:/chamados/meus";
        }

        boolean hasText = texto != null && !texto.isBlank();
        boolean hasFiles = anexosFile != null && anexosFile.length > 0 && !anexosFile[0].isEmpty();

        if (!hasText && !hasFiles) {
            redirectAttributes.addFlashAttribute("error_message", "Você precisa enviar um texto ou pelo menos um arquivo.");
            return "redirect:/chamados/" + id;
        }

        if (hasText) {
            Mensagem novaMensagem = new Mensagem();
            novaMensagem.setTexto(texto);
            novaMensagem.setAutor(autor);
            novaMensagem.setChamado(chamado);
            novaMensagem.setDataEnvio(LocalDateTime.now());
            mensagemRepository.save(novaMensagem);
        }

        if (hasFiles) {
            for (MultipartFile file : anexosFile) {
                String nomeUnico = fileStorageService.storeFile(file);
                Anexo anexo = new Anexo();
                anexo.setNomeArquivo(file.getOriginalFilename());
                anexo.setTipoArquivo(file.getContentType());
                anexo.setNomeUnico(nomeUnico);
                anexo.setChamado(chamado);
                anexoRepository.save(anexo);
            }
        }

        redirectAttributes.addFlashAttribute("success_message", "Sua mensagem foi enviada com sucesso!");
        return "redirect:/chamados/" + id;
    }
}