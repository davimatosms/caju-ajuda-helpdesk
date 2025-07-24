package br.com.cajuajuda.cajuajudadesktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Mensagem {

    private String texto;
    private LocalDateTime dataEnvio;
    private Autor autor;

    // Getters e Setters
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    /**
     * Classe interna para representar o autor da mensagem.
     * Agora inclui o campo 'role' para a estilização dos balões de chat.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Autor {
        private String nome;
        private String role; // <-- CAMPO QUE FALTAVA

        // Construtor vazio necessário para o Jackson
        public Autor() {}

        // Construtor para facilitar a criação
        public Autor(String nome, String role) {
            this.nome = nome;
            this.role = role;
        }

        // Getters e Setters para os dois campos
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
