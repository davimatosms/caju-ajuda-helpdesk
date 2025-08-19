package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.SlaRegrasFormDto;
import br.com.applogin.applogin.dto.TecnicoCadastroDto;
import br.com.applogin.applogin.model.SlaRegra;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import br.com.applogin.applogin.repository.ChamadoRepository;
import br.com.applogin.applogin.repository.SlaRegraRepository;
import br.com.applogin.applogin.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private SlaRegraRepository slaRegraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String mostrarDashboardAdmin(Model model) {
        long chamadosAbertos = chamadoRepository.countByStatus(StatusChamado.ABERTO);
        long chamadosEmAndamento = chamadoRepository.countByStatus(StatusChamado.EM_ANDAMENTO);
        long chamadosFechados = chamadoRepository.countByStatus(StatusChamado.FECHADO);

        model.addAttribute("chamadosAbertos", chamadosAbertos);
        model.addAttribute("chamadosEmAndamento", chamadosEmAndamento);
        model.addAttribute("chamadosFechados", chamadosFechados);

        return "admin-dashboard";
    }

    @GetMapping("/sla")
    public String gerenciarSla(Model model) {
        List<SlaRegra> regras = slaRegraRepository.findAll();
        SlaRegrasFormDto regrasForm = new SlaRegrasFormDto();
        regrasForm.setRegras(regras);
        model.addAttribute("regrasForm", regrasForm);
        return "admin-sla";
    }

    @PostMapping("/sla/salvar")
    public String salvarSla(@ModelAttribute SlaRegrasFormDto regrasForm, RedirectAttributes redirectAttributes) {
        try {
            slaRegraRepository.saveAll(regrasForm.getRegras());
            redirectAttributes.addFlashAttribute("success_message", "Regras de SLA salvas com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_message", "Ocorreu um erro ao salvar as regras de SLA.");
        }
        return "redirect:/admin/sla";
    }

    @GetMapping("/tecnicos")
    public String gerenciarTecnicos(Model model) {
        List<Usuario> tecnicos = usuarioRepository.findByRole(UsuarioRole.TECNICO);
        model.addAttribute("tecnicos", tecnicos);
        if (!model.containsAttribute("novoTecnico")) {
            model.addAttribute("novoTecnico", new TecnicoCadastroDto());
        }
        return "admin-tecnicos";
    }

    @PostMapping("/tecnicos/novo")
    public String salvarTecnico(@Valid @ModelAttribute("novoTecnico") TecnicoCadastroDto tecnicoDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {

        if (usuarioRepository.findByEmail(tecnicoDto.getEmail()) != null) {
            result.rejectValue("email", "email.exists", "Este e-mail já está em uso.");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.novoTecnico", result);
            redirectAttributes.addFlashAttribute("novoTecnico", tecnicoDto);
            return "redirect:/admin/tecnicos";
        }

        Usuario novoTecnico = new Usuario();
        novoTecnico.setNome(tecnicoDto.getNome());
        novoTecnico.setEmail(tecnicoDto.getEmail());
        novoTecnico.setSenha(passwordEncoder.encode(tecnicoDto.getSenha()));
        novoTecnico.setRole(UsuarioRole.TECNICO);
        novoTecnico.setEnabled(true);

        usuarioRepository.save(novoTecnico);

        redirectAttributes.addFlashAttribute("success_message", "Técnico criado com sucesso!");
        return "redirect:/admin/tecnicos";
    }
}