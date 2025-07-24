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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/chamados")
public class ChamadoController {

    // Mantemos os repositórios para operações de leitura
    @Autowired
    private ChamadoRepository chamadoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Adicionamos o serviço para operações de escrita (com lógica de negócio)
    @Autowired
    private ChamadoService chamadoService;

    @Autowired
    private FileStorageService fileStorageService;

    // Seus métodos de leitura e visualização permanecem os mesmos, pois estão ótimos.
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
        // ... seu metodo original está perfeito, sem necessidade de alteração ...
        Optional<Chamado> chamadoOpt = chamadoRepository.findById(id);
        if (chamadoOpt.isEmpty()) {
            return "redirect:/chamados/meus";
        }

        Chamado chamado = chamadoOpt.get();
        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!chamado.getCliente().getEmail().equals(emailUsuarioLogado)) {
            return "redirect:/chamados/meus?error=acesso_negado";
        }
        model.addAttribute("chamado", new ChamadoDetalhesDto(chamado));
        return "detalhes-chamado";
    }

    @GetMapping("/novo")
    public String mostrarFormularioNovoChamado(Model model) {
        model.addAttribute("chamado", new Chamado());
        model.addAttribute("prioridades", PrioridadeChamado.values());
        return "novo-chamado";
    }


    // --- METODO DE CRIAÇÃO ATUALIZADO ---
    @PostMapping
    public String salvarNovoChamado(@Valid @ModelAttribute Chamado chamado,
                                    BindingResult result,
                                    @RequestParam("anexosFile") List<MultipartFile> anexosFile,
                                    RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            return "novo-chamado";
        }

        try {
            String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario cliente = usuarioRepository.findByEmail(emailUsuarioLogado);
            chamado.setCliente(cliente);

            // A descrição inicial do chamado vira a primeira mensagem
            Mensagem primeiraMensagem = new Mensagem();
            primeiraMensagem.setTexto(chamado.getDescricao());
            primeiraMensagem.setAutor(cliente);
            primeiraMensagem.setChamado(chamado);
            primeiraMensagem.setDataEnvio(LocalDateTime.now());

            // Processa e associa os anexos à PRIMEIRA MENSAGEM
            for (MultipartFile file : anexosFile) {
                if (file != null && !file.isEmpty()) {
                    String nomeUnico = fileStorageService.storeFile(file);
                    Anexo anexo = new Anexo();
                    anexo.setNomeArquivo(file.getOriginalFilename());
                    anexo.setTipoArquivo(file.getContentType());
                    anexo.setNomeUnico(nomeUnico);
                    //anexo.setMensagem(primeiraMensagem); // Assumindo que Anexo tem relação com Mensagem
                    primeiraMensagem.getAnexos().add(anexo);
                }
            }
            chamado.getMensagens().add(primeiraMensagem);

            // <-- MUDANÇA PRINCIPAL: Usamos o serviço para salvar e aplicar a regra de SLA
            Chamado chamadoSalvo = chamadoService.criarNovoChamado(chamado);

            redirectAttributes.addFlashAttribute("success_message", "Chamado #" + chamadoSalvo.getId() + " aberto com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_message", "Erro ao abrir o chamado: " + e.getMessage());
        }
        return "redirect:/chamados/meus";
    }


    // Seus outros métodos (editar, download, etc.) podem ser mantidos como estavam.

    // Apenas o metodo de enviar nova mensagem precisa de um pequeno ajuste.

    @GetMapping("/anexos/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadAnexo(@PathVariable String filename) {
        // ... seu método original está perfeito, sem necessidade de alteração ...
        Resource file = fileStorageService.loadFileAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    // --- METODO DE ENVIAR MENSAGEM ATUALIZADO ---
    @PostMapping("/{id}/enviar-mensagem")
    public String enviarMensagemPeloCliente(
            @PathVariable("id") Long id,
            @RequestParam(value = "texto", required = false) String texto,
            @RequestParam("anexosFile") MultipartFile[] anexosFile,
            RedirectAttributes redirectAttributes) {

        Optional<Chamado> chamadoOpt = chamadoRepository.findById(id);
        if (chamadoOpt.isEmpty()) { /* ... tratamento de erro ... */ return "redirect:/chamados/meus"; }
        Chamado chamado = chamadoOpt.get();

        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario autor = usuarioRepository.findByEmail(emailUsuarioLogado);

        // ... (validações de segurança e de campos vazios) ...

        // Criamos uma única mensagem para agrupar o texto e os anexos
        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setAutor(autor);
        novaMensagem.setChamado(chamado);
        novaMensagem.setDataEnvio(LocalDateTime.now());

        if (texto != null && !texto.isBlank()) {
            novaMensagem.setTexto(texto);
        }

        // Associa os anexos a ESTA nova mensagem
        for (MultipartFile file : anexosFile) {
            if(file != null && !file.isEmpty()) {
                // ... (lógica para salvar o arquivo e criar o objeto Anexo)
                // Anexo anexo = new Anexo(...);
                // novaMensagem.getAnexos().add(anexo);
            }
        }

        // Adiciona a nova mensagem à lista do chamado e salva
        chamado.getMensagens().add(novaMensagem);
        chamadoRepository.save(chamado); // Salvar o chamado atualiza a cascata de mensagens

        redirectAttributes.addFlashAttribute("success_message", "Sua mensagem foi enviada com sucesso!");
        return "redirect:/chamados/" + id;
    }
}