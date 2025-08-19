package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class ProfileController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String mostrarPerfil(Model model, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName());
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    // ===== NOVO MÉTODO ADICIONADO AQUI =====
    @PostMapping("/alterar-dados")
    public String alterarDados(@RequestParam("nome") String novoNome,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        if (novoNome == null || novoNome.isBlank()) {
            redirectAttributes.addFlashAttribute("error_message", "O nome não pode estar em branco.");
            return "redirect:/perfil";
        }

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName());
        usuario.setNome(novoNome);
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success_message", "Seu nome foi alterado com sucesso!");
        return "redirect:/perfil";
    }
    // =======================================

    @PostMapping("/alterar-senha")
    public String alterarSenha(
            @RequestParam("senhaAtual") String senhaAtual,
            @RequestParam("novaSenha") String novaSenha,
            @RequestParam("confirmaSenha") String confirmaSenha,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName());

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            redirectAttributes.addFlashAttribute("error_message", "A senha atual está incorreta.");
            return "redirect:/perfil";
        }

        if (!novaSenha.equals(confirmaSenha)) {
            redirectAttributes.addFlashAttribute("error_message", "A nova senha e a confirmação não coincidem.");
            return "redirect:/perfil";
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success_message", "Senha alterada com sucesso!");
        return "redirect:/perfil";
    }
}