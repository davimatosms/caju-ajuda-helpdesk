package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.StatusSla;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChamadoDetalhesDto {
    private Long id;
    private String titulo;
    private String nomeCliente;
    private StatusChamado status;
    private LocalDateTime dataCriacao;
    private List<MensagemDto> mensagens;
    private StatusSla statusSla;
    private LocalDateTime dataLimiteResolucao;
    private String descricao;

    public ChamadoDetalhesDto(Chamado chamado) {
        this.id = chamado.getId();
        this.titulo = chamado.getTitulo();
        this.nomeCliente = chamado.getCliente().getNome();
        this.status = chamado.getStatus();
        this.dataCriacao = chamado.getDataCriacao();
        this.statusSla = chamado.getStatusSla();
        this.dataLimiteResolucao = chamado.getDataLimiteResolucao();
        this.descricao = chamado.getDescricao();

        if (chamado.getMensagens() != null) {
            this.mensagens = chamado.getMensagens().stream()
                    .map(MensagemDto::new)
                    .collect(Collectors.toList());
        } else {
            this.mensagens = Collections.emptyList();
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getNomeCliente() { return nomeCliente; }
    public StatusChamado getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public List<MensagemDto> getMensagens() { return mensagens; }
    public StatusSla getStatusSla() { return statusSla; }
    public LocalDateTime getDataLimiteResolucao() { return dataLimiteResolucao; }
    public String getDescricao() { return descricao; }
}