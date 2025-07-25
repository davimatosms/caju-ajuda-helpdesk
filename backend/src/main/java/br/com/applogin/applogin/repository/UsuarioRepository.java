package br.com.applogin.applogin.repository;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    Usuario findByEmail(String email);

    List<Usuario> findByRole(UsuarioRole role);

    Usuario findByVerificationToken(String token);
}