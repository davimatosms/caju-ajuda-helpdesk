package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.StatusChamado;
import br.com.applogin.applogin.model.StatusSla;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Este DTO é usado para enviar todos os detalhes de um chamado para a API e para as views web
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetalhesChamadoDto {
    private Long id;
    private String titulo;
    private String descricao;
    private String nomeCliente;
    private StatusChamado status;
    private LocalDateTime dataCriacao;
    private List<MensagemDto> mensagens;
    private StatusSla statusSla;
    private LocalDateTime dataLimiteResolucao;

    public DetalhesChamadoDto(Chamado chamado) {
        this.id = chamado.getId();
        this.titulo = chamado.getTitulo();
        this.nomeCliente = chamado.getCliente().getNome();
        this.status = chamado.getStatus();
        this.dataCriacao = chamado.getDataCriacao();
        // A primeira mensagem contém a descrição original
        this.descricao = chamado.getMensagens() != null && !chamado.getMensagens().isEmpty()
                ? chamado.getMensagens().get(0).getTexto()
                : chamado.getDescricao();
        this.mensagens = chamado.getMensagens().stream()
                .map(MensagemDto::new)
                .collect(Collectors.toList());
        this.statusSla = chamado.getStatusSla();
        this.dataLimiteResolucao = chamado.getDataLimiteResolucao();
    }

    // Getters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getNomeCliente() { return nomeCliente; }
    public StatusChamado getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public List<MensagemDto> getMensagens() { return mensagens; }
    public StatusSla getStatusSla() { return statusSla; }
    public LocalDateTime getDataLimiteResolucao() { return dataLimiteResolucao; }
}