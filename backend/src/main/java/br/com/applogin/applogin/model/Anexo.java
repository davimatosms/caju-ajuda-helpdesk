package br.com.applogin.applogin.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Anexo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;
    private String tipoArquivo;
    private String nomeUnico;

    // --- CORREÇÃO AQUI ---
    // Um anexo pertence a uma mensagem. Esta é a ligação correta.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensagem_id", nullable = false) // Garante que a coluna no DB se chama mensagem_id
    @JsonBackReference
    private Mensagem mensagem;

    // Removemos a referência direta e incorreta ao Chamado
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "chamado_id")
    // private Chamado chamado;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getTipoArquivo() { return tipoArquivo; }
    public void setTipoArquivo(String tipoArquivo) { this.tipoArquivo = tipoArquivo; }
    public String getNomeUnico() { return nomeUnico; }
    public void setNomeUnico(String nomeUnico) { this.nomeUnico = nomeUnico; }

    // --- NOVOS GETTERS E SETTERS ---
    public Mensagem getMensagem() { return mensagem; }
    public void setMensagem(Mensagem mensagem) { this.mensagem = mensagem; }
}