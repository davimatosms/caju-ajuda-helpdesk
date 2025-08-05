package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.PrioridadeChamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.StatusSla; // <-- Importar o novo Enum

import java.time.LocalDateTime;

// DTO simples para a lista de chamados
public class ChamadoDto {
    private Long id;
    private String titulo;
    private String nomeCliente;
    private StatusChamado status;
    private PrioridadeChamado prioridade;
    private LocalDateTime dataCriacao;
    private StatusSla statusSla; // <-- NOVO CAMPO

    // Construtor que converte uma Entidade Chamado para este DTO
    public ChamadoDto(Chamado chamado) {
        this.id = chamado.getId();
        this.titulo = chamado.getTitulo();
        this.nomeCliente = chamado.getCliente().getNome();
        this.status = chamado.getStatus();
        this.prioridade = chamado.getPrioridade();
        this.dataCriacao = chamado.getDataCriacao();
        this.statusSla = chamado.getStatusSla(); // <-- ADICIONAR MAPEAMENTO
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public StatusChamado getStatus() { return status; }
    public void setStatus(StatusChamado status) { this.status = status; }
    public PrioridadeChamado getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeChamado prioridade) { this.prioridade = prioridade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    // <-- ADICIONAR GETTER E SETTER PARA O NOVO CAMPO
    public StatusSla getStatusSla() { return statusSla; }
    public void setStatusSla(StatusSla statusSla) { this.statusSla = statusSla; }
}