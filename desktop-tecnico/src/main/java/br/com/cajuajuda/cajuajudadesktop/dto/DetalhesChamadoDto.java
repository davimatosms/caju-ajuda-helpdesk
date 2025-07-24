package br.com.cajuajuda.cajuajudadesktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DetalhesChamadoDto {
    private Long id;
    private String titulo;
    private String nomeCliente;
    private StatusChamado status; // <-- CAMPO ADICIONADO
    private List<MensagemDto> mensagens;
    private String statusSla;
    private LocalDateTime dataLimiteResolucao;

    // Construtor vazio para o Jackson (deserializador JSON)
    public DetalhesChamadoDto() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    // Getter e Setter para o campo Status
    public StatusChamado getStatus() { return status; }
    public void setStatus(StatusChamado status) { this.status = status; }

    public List<MensagemDto> getMensagens() { return mensagens; }
    public void setMensagens(List<MensagemDto> mensagens) { this.mensagens = mensagens; }
    public String getStatusSla() { return statusSla; }
    public void setStatusSla(String statusSla) { this.statusSla = statusSla; }
    public LocalDateTime getDataLimiteResolucao() { return dataLimiteResolucao; }
    public void setDataLimiteResolucao(LocalDateTime dataLimiteResolucao) { this.dataLimiteResolucao = dataLimiteResolucao; }
}