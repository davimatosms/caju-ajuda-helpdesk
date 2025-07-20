package br.com.applogin.applogin.dto;

// DTO para a mensagem que chega do cliente via WebSocket
public class ChatMessageDto {
    private String texto;
    private Long chamadoId;

    // Getters e Setters
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public Long getChamadoId() { return chamadoId; }
    public void setChamadoId(Long chamadoId) { this.chamadoId = chamadoId; }
}