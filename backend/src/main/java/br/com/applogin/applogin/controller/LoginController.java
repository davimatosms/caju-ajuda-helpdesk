package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository ur;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Value("${app.base-url}")
    private String appBaseUrl;

    @GetMapping("/login")
    public String login(Model model){
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return "login";
    }

    @GetMapping("/")
    public String dashboard(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Usuario usuario = ur.findByEmail(userEmail);

        String primeiroNome = "Usuário";
        if (usuario != null && usuario.getNome() != null && !usuario.getNome().isBlank()) {
            primeiroNome = usuario.getNome().split(" ")[0];
        }
        model.addAttribute("nomeUsuario", primeiroNome);

        return "index";
    }

    @GetMapping("/cadastroUsuario")
    public String mostrarFormularioCadastro(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return "cadastro";
    }

    @PostMapping("/cadastroUsuario")
    public String cadastroUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                  BindingResult result,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.usuario", result);
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/cadastroUsuario";
        }
        if (ur.findByEmail(usuario.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("error_message", "Este e-mail já está em uso.");
            return "redirect:/cadastroUsuario";
        }

        usuario.setRole(UsuarioRole.CLIENTE);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setEnabled(false);
        usuario.setVerificationToken(UUID.randomUUID().toString());
        ur.save(usuario);

        try {
            String verificationUrl = appBaseUrl + "/verify?token=" + usuario.getVerificationToken();
            emailService.enviarEmailDeVerificacao(usuario.getEmail(), usuario.getNome(), verificationUrl);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error_message", "Utilizador registado, mas houve um erro ao enviar o e-mail de verificação.");
        }

        return "registro-sucesso";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("token") String token, Model model) {
        Usuario usuario = ur.findByVerificationToken(token);
        if (usuario == null || usuario.isEnabled()) {
            model.addAttribute("error_message", "Token de verificação inválido ou já utilizado.");
            return "login";
        } else {
            usuario.setEnabled(true);
            usuario.setVerificationToken(null);
            ur.save(usuario);
            model.addAttribute("success_message", "Sua conta foi verificada com sucesso!");
            return "verificacao-sucesso";
        }
    }

    // --- NOVO MÉTODO PARA O APP MOBILE CHAMAR ---
    @PostMapping("/api/reenviar-verificacao")
    @ResponseBody
    public ResponseEntity<?> reenviarEmailDeVerificacao(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Usuario usuario = ur.findByEmail(email);

        if (usuario != null && !usuario.isEnabled()) {
            try {
                String verificationUrl = appBaseUrl + "/verify?token=" + usuario.getVerificationToken();
                emailService.enviarEmailDeVerificacao(usuario.getEmail(), usuario.getNome(), verificationUrl);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.status(500).build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
}