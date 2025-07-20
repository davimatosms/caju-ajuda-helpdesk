package br.com.cajuajuda.cajuajudadesktop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Chamado {

    private Long id;
    private String titulo;
    private String descricao;
    private StatusChamado status;
    private PrioridadeChamado prioridade;
    private LocalDateTime dataCriacao;
    private Usuario cliente;
    private List<Anexo> anexos;
    private List<Mensagem> mensagens;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusChamado getStatus() { return status; }
    public void setStatus(StatusChamado status) { this.status = status; }
    public PrioridadeChamado getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeChamado prioridade) { this.prioridade = prioridade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }
    public List<Anexo> getAnexos() { return anexos; }
    public void setAnexos(List<Anexo> anexos) { this.anexos = anexos; }

    // GETTER E SETTER
    public List<Mensagem> getMensagens() { return mensagens; }
    public void setMensagens(List<Mensagem> mensagens) { this.mensagens = mensagens; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usuario {
        private String nome;
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }
}