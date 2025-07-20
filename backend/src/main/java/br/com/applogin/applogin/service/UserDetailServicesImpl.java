package br.com.applogin.applogin.service;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailServicesImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // A única responsabilidade desta classe é encontrar o usuário e convertê-lo.
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name());

        return new User(usuario.getEmail(), usuario.getSenha(), Collections.singletonList(authority));
    }
}