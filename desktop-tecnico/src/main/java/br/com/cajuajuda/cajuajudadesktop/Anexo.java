package br.com.cajuajuda.cajuajudadesktop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Anexo {
    private String nomeArquivo;
    private String nomeUnico;

    // Getters e Setters
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getNomeUnico() { return nomeUnico; }
    public void setNomeUnico(String nomeUnico) { this.nomeUnico = nomeUnico; }

    // Usado para exibir o nome do arquivo na lista
    @Override
    public String toString() {
        return nomeArquivo;
    }
}