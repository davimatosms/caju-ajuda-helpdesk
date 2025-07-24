package br.com.cajuajuda.cajuajudadesktop.dto;

public class RespostaTecnicoDto {
    private String resposta;
    private StatusChamado novoStatus;

    // Getters e Setters
    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }
    public StatusChamado getNovoStatus() { return novoStatus; }
    public void setNovoStatus(StatusChamado novoStatus) { this.novoStatus = novoStatus; }
}