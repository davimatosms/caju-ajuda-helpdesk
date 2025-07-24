package br.com.cajuajuda.cajuajudadesktop.dto;

// Este DTO é usado para enviar o novo status para a API do backend.
public class StatusUpdateDto {

    private StatusChamado novoStatus;

    // Getters e Setters
    public StatusChamado getNovoStatus() {
        return novoStatus;
    }

    public void setNovoStatus(StatusChamado novoStatus) {
        this.novoStatus = novoStatus;
    }
}