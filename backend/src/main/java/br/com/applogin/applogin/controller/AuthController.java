package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.UsuarioRepository;
import br.com.applogin.applogin.service.JwtService;
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

// DTO para receber os dados de login via JSON
class LoginRequest {
    private String email;
    private String senha;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}

// DTO para enviar a resposta com o token via JSON
class LoginResponse {
    private final String token;
    public LoginResponse(String token) { this.token = token; }
    // Getter
    public String getToken() { return token; }
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

    /**
     * Endpoint de login oficial para a API.
     * Recebe um email e senha, e se forem válidos, retorna um Token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Pede ao Spring Security para autenticar as credenciais
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );

            // 2. Se a autenticação foi bem-sucedida, busca os detalhes do usuário
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

            // 3. Gera o token JWT para este usuário
            final String jwt = jwtService.generateToken(userDetails);

            // 4. Retorna o token em uma resposta 200 OK
            return ResponseEntity.ok(new LoginResponse(jwt));

        } catch (AuthenticationException e) {
            // 5. Se a autenticação falhou, retorna 401 Unauthorized
            return ResponseEntity.status(401).body("E-mail ou senha inválidos.");
        }
    }

    /**
     * Endpoint temporário de DEPURAÇÃO.
     * Ele bypassa o fluxo do Spring Security e testa diretamente se a senha corresponde.
     */
    @PostMapping("/check-password")
    public ResponseEntity<?> checkPassword(@RequestBody LoginRequest loginRequest) {
        System.out.println(">>> Recebido pedido para /api/auth/check-password para o email: " + loginRequest.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuario == null) {
            System.out.println(">>> Usuário não encontrado no banco de dados.");
            return ResponseEntity.ok(Map.of("userFound", false, "passwordMatches", false));
        }

        System.out.println(">>> Usuário encontrado. Senha hash no BD: " + usuario.getSenha());

        boolean matches = passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha());

        System.out.println(">>> Resultado da verificação da senha: " + matches);

        return ResponseEntity.ok(Map.of("userFound", true, "passwordMatches", matches));
    }
}