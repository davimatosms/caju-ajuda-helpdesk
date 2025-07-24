package br.com.cajuajuda.cajuajudadesktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MensagemDto {
    private String texto;
    private LocalDateTime dataEnvio;
    private UsuarioDto autor;

    // Getters e Setters
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
    public UsuarioDto getAutor() { return autor; }
    public void setAutor(UsuarioDto autor) { this.autor = autor; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsuarioDto {
        private String nome;
        private String role;
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}