package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Mensagem;
import java.time.LocalDateTime;

public class MensagemDto {
    private Long id;
    private String texto;
    private LocalDateTime dataEnvio;
    private AutorDto autor; // <-- CORRIGIDO: Agora usa AutorDto

    public MensagemDto(Mensagem mensagem) {
        this.id = mensagem.getId();
        this.texto = mensagem.getTexto();
        this.dataEnvio = mensagem.getDataEnvio();
        // <-- CORRIGIDO: Cria um novo AutorDto, garantindo a estrutura correta
        this.autor = new AutorDto(mensagem.getAutor());
    }

    // Getters e Setters atualizados
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
    public AutorDto getAutor() { return autor; }
    public void setAutor(AutorDto autor) { this.autor = autor; }
}