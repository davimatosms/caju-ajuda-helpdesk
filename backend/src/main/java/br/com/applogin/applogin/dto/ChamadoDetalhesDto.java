package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.StatusSla;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ChamadoDetalhesDto {
    private Long id;
    private String titulo;
    private String descricao;
    private String nomeCliente;
    private StatusChamado status;
    private LocalDateTime dataCriacao; // <-- CAMPO QUE ESTAVA FALTANDO
    private List<MensagemDto> mensagens;
    private StatusSla statusSla;
    private LocalDateTime dataLimiteResolucao;

    public ChamadoDetalhesDto(Chamado chamado) {
        this.id = chamado.getId();
        this.titulo = chamado.getTitulo();
        this.nomeCliente = chamado.getCliente().getNome();
        this.status = chamado.getStatus();
        this.dataCriacao = chamado.getDataCriacao(); // <-- MAPEAMENTO ADICIONADO
        this.descricao = chamado.getMensagens().isEmpty() ? chamado.getDescricao() : chamado.getMensagens().get(0).getTexto();
        this.mensagens = chamado.getMensagens().stream().map(MensagemDto::new).collect(Collectors.toList());
        this.statusSla = chamado.getStatusSla();
        this.dataLimiteResolucao = chamado.getDataLimiteResolucao();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public StatusChamado getStatus() { return status; }
    public void setStatus(StatusChamado status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public List<MensagemDto> getMensagens() { return mensagens; }
    public void setMensagens(List<MensagemDto> mensagens) { this.mensagens = mensagens; }
    public StatusSla getStatusSla() { return statusSla; }
    public void setStatusSla(StatusSla statusSla) { this.statusSla = statusSla; }
    public LocalDateTime getDataLimiteResolucao() { return dataLimiteResolucao; }
    public void setDataLimiteResolucao(LocalDateTime dataLimiteResolucao) { this.dataLimiteResolucao = dataLimiteResolucao; }
}