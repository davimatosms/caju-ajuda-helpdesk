package br.com.applogin.applogin.dto;

import java.util.List;

// DTO principal que agrupa todas as métricas para o dashboard
public class MetricasDto {

    private long chamadosAbertos;
    private long chamadosEmAndamento;
    private long chamadosFechados;
    private List<MetricaPorTecnicoDto> performanceTecnicos;

    // Getters e Setters
    public long getChamadosAbertos() { return chamadosAbertos; }
    public void setChamadosAbertos(long chamadosAbertos) { this.chamadosAbertos = chamadosAbertos; }
    public long getChamadosEmAndamento() { return chamadosEmAndamento; }
    public void setChamadosEmAndamento(long chamadosEmAndamento) { this.chamadosEmAndamento = chamadosEmAndamento; }
    public long getChamadosFechados() { return chamadosFechados; }
    public void setChamadosFechados(long chamadosFechados) { this.chamadosFechados = chamadosFechados; }
    public List<MetricaPorTecnicoDto> getPerformanceTecnicos() { return performanceTecnicos; }
    public void setPerformanceTecnicos(List<MetricaPorTecnicoDto> performanceTecnicos) { this.performanceTecnicos = performanceTecnicos; }
}