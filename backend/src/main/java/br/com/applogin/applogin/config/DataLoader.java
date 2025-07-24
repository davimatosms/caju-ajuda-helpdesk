package br.com.applogin.applogin.config;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // --- Cria o usuário ADMIN padrão, se não existir ---
        if (usuarioRepository.findByRole(UsuarioRole.ADMIN).isEmpty()) {
            System.out.println("Criando usuário ADMIN padrão...");
            Usuario admin = new Usuario();
            admin.setNome("Admin Caju");
            admin.setEmail("admin@cajuajuda.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setRole(UsuarioRole.ADMIN);
            admin.setEnabled(true);
            usuarioRepository.save(admin);
            System.out.println("Usuário ADMIN padrão criado com sucesso: admin@cajuajuda.com / admin123");
        }

        // --- Cria o usuário TECNICO padrão, se não existir ---
        if (usuarioRepository.findByEmail("tecnico@cajuajuda.com") == null) {
            System.out.println("Criando usuário TECNICO padrão...");
            Usuario tecnico = new Usuario();
            tecnico.setNome("Técnico Padrão");
            tecnico.setEmail("tecnico@cajuajuda.com");
            tecnico.setSenha(passwordEncoder.encode("senha123"));
            tecnico.setRole(UsuarioRole.TECNICO);
            tecnico.setEnabled(true); // Garante que o técnico já nasce ativo!
            usuarioRepository.save(tecnico);
            System.out.println("Usuário TECNICO padrão criado com sucesso: tecnico@cajuajuda.com / senha123");
        }
    }
}