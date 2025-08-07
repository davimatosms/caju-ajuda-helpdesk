package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Usuario;
import br.com.applogin.applogin.model.UsuarioRole;

public class AutorDto {
    private String nome;
    private UsuarioRole role;

    public AutorDto(Usuario autor) {
        this.nome = autor.getNome();
        this.role = autor.getRole();
    }

    // Getters
    public String getNome() { return nome; }
    public UsuarioRole getRole() { return role; }
}