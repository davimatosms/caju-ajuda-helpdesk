package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.StatusChamado;

// DTO para receber o novo status via API
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