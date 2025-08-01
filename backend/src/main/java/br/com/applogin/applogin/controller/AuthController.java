package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.EmailService;
import br.com.applogin.applogin.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

// DTOs para os requests JSON
class LoginRequest {
    private String email;
    private String senha;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}

class LoginResponse {
    private final String token;
    public LoginResponse(String token) { this.token = token; }
    public String getToken() { return token; }
}

// --- NOVO DTO PARA O REGISTO ---
class RegistroRequest {
    private String nome;
    private String email;
    private String senha;
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            final String jwt = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(new LoginResponse(jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("E-mail ou senha inválidos.");
        }
    }

    // --- NOVO MÉTODO PARA REGISTO VIA API ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistroRequest registroRequest, HttpServletRequest request) {
        if (usuarioRepository.findByEmail(registroRequest.getEmail()) != null) {
            return ResponseEntity.status(400).body("Este e-mail já está em uso.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registroRequest.getNome());
        novoUsuario.setEmail(registroRequest.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(registroRequest.getSenha()));
        novoUsuario.setRole(UsuarioRole.CLIENTE);
        novoUsuario.setEnabled(false);
        novoUsuario.setVerificationToken(UUID.randomUUID().toString());

        usuarioRepository.save(novoUsuario);

        try {
            // Constrói a URL base para o e-mail de verificação
            String siteURL = request.getRequestURL().toString()
                    .replace(request.getServletPath(), "");
            String verificationUrl = siteURL.replace("/register", "") + "/verify?token=" + novoUsuario.getVerificationToken();

            emailService.enviarEmailDeVerificacao(novoUsuario.getEmail(), novoUsuario.getNome(), verificationUrl);

            return ResponseEntity.ok(Map.of("message", "Utilizador registado com sucesso! Verifique o seu e-mail."));

        } catch (Exception e) {
            // Mesmo que o e-mail falhe, o utilizador foi criado.
            return ResponseEntity.status(500).body("Utilizador criado, mas falha ao enviar e-mail de verificação.");
        }
    }
}