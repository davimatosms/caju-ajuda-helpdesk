package br.com.applogin.applogin.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "chamado")
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private LocalDateTime dataCriacao;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento; // <-- NOVO CAMPO

    @Enumerated(EnumType.STRING)
    private StatusChamado status;

    @Enumerated(EnumType.STRING)
    private PrioridadeChamado prioridade;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @OneToMany(mappedBy = "chamado", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("dataEnvio ASC")
    @JsonManagedReference
    private List<Mensagem> mensagens = new ArrayList<>();

    @Column
    private LocalDateTime dataLimitePrimeiraResposta;

    @Column
    private LocalDateTime dataLimiteResolucao;

    @Column(name = "categoria")
    private String categoria;

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Enumerated(EnumType.STRING)
    private StatusSla statusSla = StatusSla.NO_PRAZO;

    public Chamado() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataFechamento() { return dataFechamento; } // <-- NOVO GETTER E SETTER
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }
    public StatusChamado getStatus() { return status; }
    public void setStatus(StatusChamado status) { this.status = status; }
    public PrioridadeChamado getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeChamado prioridade) { this.prioridade = prioridade; }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }
    public List<Mensagem> getMensagens() { return mensagens; }
    public void setMensagens(List<Mensagem> mensagens) { this.mensagens = mensagens; }
    public LocalDateTime getDataLimitePrimeiraResposta() { return dataLimitePrimeiraResposta; }
    public void setDataLimitePrimeiraResposta(LocalDateTime dataLimitePrimeiraResposta) { this.dataLimitePrimeiraResposta = dataLimitePrimeiraResposta; }
    public LocalDateTime getDataLimiteResolucao() { return dataLimiteResolucao; }
    public void setDataLimiteResolucao(LocalDateTime dataLimiteResolucao) { this.dataLimiteResolucao = dataLimiteResolucao; }
    public StatusSla getStatusSla() { return statusSla; }
    public void setStatusSla(StatusSla statusSla) { this.statusSla = statusSla; }

    public List<Anexo> getAnexos() {
        if (this.mensagens == null) {
            return new ArrayList<>();
        }
        return this.mensagens.stream()
                .flatMap(mensagem -> mensagem.getAnexos().stream())
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chamado chamado = (Chamado) o;
        return Objects.equals(id, chamado.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        status = StatusChamado.ABERTO;
    }
}