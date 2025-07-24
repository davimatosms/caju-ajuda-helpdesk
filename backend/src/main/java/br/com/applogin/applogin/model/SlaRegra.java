package br.com.applogin.applogin.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sla_regras")
public class SlaRegra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PrioridadeChamado prioridade;

    @Column(nullable = false)
    private Integer tempoRespostaHoras;

    @Column(nullable = false)
    private Integer tempoResolucaoHoras;

    // Construtor sem argumentos (gerado por @NoArgsConstructor)
    public SlaRegra() {
    }

    // Construtor com todos os argumentos (gerado por @AllArgsConstructor)
    public SlaRegra(Long id, PrioridadeChamado prioridade, Integer tempoRespostaHoras, Integer tempoResolucaoHoras) {
        this.id = id;
        this.prioridade = prioridade;
        this.tempoRespostaHoras = tempoRespostaHoras;
        this.tempoResolucaoHoras = tempoResolucaoHoras;
    }

    // Getters e Setters (gerados por @Data)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrioridadeChamado getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeChamado prioridade) {
        this.prioridade = prioridade;
    }

    public Integer getTempoRespostaHoras() {
        return tempoRespostaHoras;
    }

    public void setTempoRespostaHoras(Integer tempoRespostaHoras) {
        this.tempoRespostaHoras = tempoRespostaHoras;
    }

    public Integer getTempoResolucaoHoras() {
        return tempoResolucaoHoras;
    }

    public void setTempoResolucaoHoras(Integer tempoResolucaoHoras) {
        this.tempoResolucaoHoras = tempoResolucaoHoras;
    }

    // Métodos equals e hashCode (gerados por @Data)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaRegra slaRegra = (SlaRegra) o;
        return Objects.equals(id, slaRegra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Método toString (gerado por @Data)
    @Override
    public String toString() {
        return "SlaRegra{" +
                "id=" + id +
                ", prioridade=" + prioridade +
                ", tempoRespostaHoras=" + tempoRespostaHoras +
                ", tempoResolucaoHoras=" + tempoResolucaoHoras +
                '}';
    }
}