package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.Anexo;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.PrioridadeChamado;
import br.com.applogin.applogin.model.StatusChamado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Este é o DTO principal que será retornado pela API
public class ChamadoDetalhesDto {
    private Long id;
    private String titulo;
    private String descricao; // Descrição original
    private StatusChamado status;
    private PrioridadeChamado prioridade;
    private LocalDateTime dataCriacao;
    private UsuarioDto cliente;
    private List<Anexo> anexos;
    private List<MensagemDto> mensagens;

    public ChamadoDetalhesDto(Chamado chamado) {
        this.id = chamado.getId();
        this.titulo = chamado.getTitulo();
        this.descricao = chamado.getDescricao();
        this.status = chamado.getStatus();
        this.prioridade = chamado.getPrioridade();
        this.dataCriacao = chamado.getDataCriacao();
        this.cliente = new UsuarioDto(chamado.getCliente().getNome(), chamado.getCliente().getRole().name());
        this.anexos = chamado.getAnexos();
        this.mensagens = chamado.getMensagens().stream().map(MensagemDto::new).collect(Collectors.toList());
    }

    // Adicione Getters e Setters para todos os campos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusChamado getStatus() {
        return status;
    }

    public void setStatus(StatusChamado status) {
        this.status = status;
    }

    public PrioridadeChamado getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeChamado prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public UsuarioDto getCliente() {
        return cliente;
    }

    public void setCliente(UsuarioDto cliente) {
        this.cliente = cliente;
    }

    public List<Anexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<Anexo> anexos) {
        this.anexos = anexos;
    }

    public List<MensagemDto> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<MensagemDto> mensagens) {
        this.mensagens = mensagens;
    }
}