package br.com.applogin.applogin.dto;


public class UsuarioDto {
    private String nome;
    private String role;

    public UsuarioDto(String nome, String role) {
        this.nome = nome;
        this.role = role;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}