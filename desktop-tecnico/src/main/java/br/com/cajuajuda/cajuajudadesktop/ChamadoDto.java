package br.com.cajuajuda.cajuajudadesktop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ChamadoDto {
    private Long id;
    private String titulo;
    private String nomeCliente;
    private StatusChamado status;
    private PrioridadeChamado prioridade;
    private LocalDateTime dataCriacao;

    // Getters e Setters para todos os campos...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public StatusChamado getStatus() { return status; }
    public void setStatus(StatusChamado status) { this.status = status; }
    public PrioridadeChamado getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeChamado prioridade) { this.prioridade = prioridade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}