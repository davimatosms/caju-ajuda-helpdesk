package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Mensagem;
import java.time.LocalDateTime;

public class MensagemDto {
    private Long id;
    private String texto;
    private LocalDateTime dataEnvio;
    private UsuarioDto autor;

    public MensagemDto(Mensagem mensagem) {
        this.id = mensagem.getId();
        this.texto = mensagem.getTexto();
        this.dataEnvio = mensagem.getDataEnvio();
        this.autor = new UsuarioDto(mensagem.getAutor().getNome(), mensagem.getAutor().getRole().name());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
    public UsuarioDto getAutor() { return autor; }
    public void setAutor(UsuarioDto autor) { this.autor = autor; }
}