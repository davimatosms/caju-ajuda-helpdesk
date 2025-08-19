package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Usuario;

public class UsuarioDetalhesDto {
    private String nome;
    private String email;

    public UsuarioDetalhesDto(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}