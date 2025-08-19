package br.com.applogin.applogin.dto;

import br.com.applogin.applogin.model.SlaRegra;
import java.util.List;

public class SlaRegrasFormDto {

    private List<SlaRegra> regras;

    // Getters e Setters
    public List<SlaRegra> getRegras() {
        return regras;
    }

    public void setRegras(List<SlaRegra> regras) {
        this.regras = regras;
    }
}