package br.com.cajuajuda.cajuajudadesktop;

public class ChatMessageDto {
    private String texto;
    private Long chamadoId;

    // Getters e Setters
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public Long getChamadoId() { return chamadoId; }
    public void setChamadoId(Long chamadoId) { this.chamadoId = chamadoId; }
}