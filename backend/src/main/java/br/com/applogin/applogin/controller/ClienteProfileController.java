package br.com.applogin.applogin.controller;

import br.com.applogin.applogin.dto.UpdateSenhaDto;
import br.com.applogin.applogin.dto.UsuarioDetalhesDto;
import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cliente/perfil")
public class ClienteProfileController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint para BUSCAR os dados do perfil do utilizador logado
    @GetMapping
    public ResponseEntity<UsuarioDetalhesDto> getPerfil(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName());
        return ResponseEntity.ok(new UsuarioDetalhesDto(usuario));
    }

    // Endpoint para ALTERAR A SENHA
    @PostMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(@RequestBody UpdateSenhaDto updateSenhaDto, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName());

        // 1. Verifica se a senha atual fornecida corresponde à senha no banco de dados
        if (!passwordEncoder.matches(updateSenhaDto.getSenhaAtual(), usuario.getSenha())) {
            return ResponseEntity.badRequest().body("A senha atual está incorreta.");
        }

        // 2. Codifica e salva a nova senha
        usuario.setSenha(passwordEncoder.encode(updateSenhaDto.getNovaSenha()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Senha alterada com sucesso!");
    }
}