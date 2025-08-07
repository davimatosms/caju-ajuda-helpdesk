package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.PrioridadeChamado;

// DTO para receber os dados de um novo chamado vindo da API mobile.
public class NovoChamadoDto {
    private String titulo;
    private String descricao;
    private PrioridadeChamado prioridade;


    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public PrioridadeChamado getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeChamado prioridade) { this.prioridade = prioridade; }
}