package br.com.applogin.applogin.dto;

// DTO para representar a contagem de chamados por técnico
public class MetricaPorTecnicoDto {
    private String nomeTecnico;
    private long totalChamadosFechados;

    public MetricaPorTecnicoDto(String nomeTecnico, long totalChamadosFechados) {
        this.nomeTecnico = nomeTecnico;
        this.totalChamadosFechados = totalChamadosFechados;
    }

    // Getters e Setters
    public String getNomeTecnico() { return nomeTecnico; }
    public void setNomeTecnico(String nomeTecnico) { this.nomeTecnico = nomeTecnico; }
    public long getTotalChamadosFechados() { return totalChamadosFechados; }
    public void setTotalChamadosFechados(long totalChamadosFechados) { this.totalChamadosFechados = totalChamadosFechados; }
}