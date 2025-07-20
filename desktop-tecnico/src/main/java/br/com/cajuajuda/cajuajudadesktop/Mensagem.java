package br.com.cajuajuda.cajuajudadesktop;

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

    // Classe interna para representar o autor da mensagem
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Autor {
        private String nome;
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }
}