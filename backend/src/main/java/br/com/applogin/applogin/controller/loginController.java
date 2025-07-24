package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class LoginController { // Nome da classe corrigido para convenção Java (LoginController)

    @Autowired
    private UsuarioRepository ur;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String login(Model model){
        // Adiciona um objeto 'usuario' vazio ao model para o data-binding do formulário
        model.addAttribute("usuario", new Usuario());
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

    // Método para exibir o formulário de cadastro
    @GetMapping("/cadastroUsuario")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastroUsuario")
    public String cadastroUsuario(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.usuario", result);
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/cadastroUsuario?error";
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
            String baseUrl = request.getRequestURL().toString().replace(request.getServletPath(), "");
            String verificationUrl = baseUrl + "/verify?token=" + usuario.getVerificationToken();

            // Chamada correta com os 3 argumentos String
            emailService.enviarEmailDeVerificacao(usuario.getEmail(), usuario.getNome(), verificationUrl);

            redirectAttributes.addFlashAttribute("success_message", "Cadastro realizado com sucesso! Verifique seu e-mail para ativar a conta.");
        } catch (Exception e) {
            // Adicionar log do erro é uma boa prática
            redirectAttributes.addFlashAttribute("error_message", "Usuário cadastrado, mas houve um erro ao enviar o e-mail de verificação.");
        }

        return "redirect:/login";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        Usuario usuario = ur.findByVerificationToken(token);

        if (usuario == null || usuario.isEnabled()) {
            redirectAttributes.addFlashAttribute("error_message", "Token de verificação inválido ou já utilizado.");
            return "redirect:/login";
        } else {
            usuario.setEnabled(true);
            usuario.setVerificationToken(null); // Limpa o token após o uso
            ur.save(usuario);
            redirectAttributes.addFlashAttribute("success_message", "Sua conta foi verificada com sucesso! Você já pode fazer o login.");
            return "redirect:/login";
        }
    }
}