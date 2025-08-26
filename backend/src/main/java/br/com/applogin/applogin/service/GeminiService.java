package br.com.applogin.applogin.service;

import br.com.applogin.applogin.dto.AnaliseChamadoDto;
import br.com.applogin.applogin.model.Chamado;
import br.com.applogin.applogin.model.PrioridadeChamado;
import br.com.applogin.applogin.repository.ChamadoRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    @Autowired
    private ChamadoRepository chamadoRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * Analisa um chamado para determinar sua Categoria e Prioridade em uma única chamada.
     */
    public AnaliseChamadoDto analisarChamado(Chamado chamado) {
        String categoriasValidas = "Transação Recusada, Chargeback e Disputas, Dúvidas sobre Reembolso, Problemas com API/Integração, Falha na Maquininha (POS), Dúvidas sobre Taxas e Repasses, Problemas com a Conta/Cadastro, Relatórios e Dashboard, Outros";
        String prioridadesValidas = "BAIXA, MEDIA, ALTA";

        String prompt = String.format(
                "Você é um classificador de tickets de suporte para um gateway de pagamentos (CajuPay). Analise o título e a mensagem de um chamado. " +
                        "Responda com um objeto JSON contendo duas chaves: 'categoria' e 'prioridade'. " +
                        "A 'categoria' deve ser UMA das seguintes: [%s]. " +
                        "A 'prioridade' deve ser UMA das seguintes: [%s], baseada na urgência e impacto do problema (ex: 'não consigo vender' é ALTA, 'dúvida sobre relatório' é BAIXA). " +
                        "O JSON deve ter o formato: {\"categoria\": \"...\", \"prioridade\": \"...\"}\n\n" +
                        "--- CHAMADO ---\n" +
                        "Título: %s\n" +
                        "Mensagem: %s\n" +
                        "--- FIM ---\n\n" +
                        "JSON de resposta:",
                categoriasValidas, prioridadesValidas,
                chamado.getTitulo(),
                chamado.getMensagens().get(0).getTexto()
        );

        String respostaJson = chamarApiGemini(prompt, "{\"categoria\": \"Outros\", \"prioridade\": \"BAIXA\"}");

        // LINHA DE DEPURAÇÃO PARA VER A RESPOSTA DA IA
        System.out.println("--- RESPOSTA CRUA DA IA ---");
        System.out.println(respostaJson);
        System.out.println("---------------------------");

        try {
            // Converte a string JSON para nosso objeto DTO
            return objectMapper.readValue(respostaJson.trim(), AnaliseChamadoDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            // Retorna um objeto padrão em caso de erro no parse do JSON
            AnaliseChamadoDto dtoPadrao = new AnaliseChamadoDto();
            dtoPadrao.setCategoria("Outros");
            dtoPadrao.setPrioridade(PrioridadeChamado.BAIXA);
            return dtoPadrao;
        }
    }

    /**
     * Gera uma sugestão de resposta para o técnico com base no histórico do chamado.
     */
    public String gerarSugestaoDeResposta(Long chamadoId) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));

        String prompt = construirPromptDeSugestao(chamado);
        return chamarApiGemini(prompt, "Não foi possível obter uma sugestão do Gemini.");
    }

    /**
     * Método central para fazer a chamada à API do Gemini.
     */
    private String chamarApiGemini(String prompt, String respostaPadrao) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", Collections.singletonList(textPart));
        Map<String, Object> requestBody = Map.of("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            GeminiResponse response = restTemplate.postForObject(apiUrl, requestEntity, GeminiResponse.class);

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                if (response.getCandidates().get(0).getContent() != null && response.getCandidates().get(0).getContent().getParts() != null && !response.getCandidates().get(0).getContent().getParts().isEmpty()) {
                    return response.getCandidates().get(0).getContent().getParts().get(0).getText();
                }
            }
            return respostaPadrao;
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao comunicar com a API do Gemini: " + e.getMessage();
        }
    }

    private String construirPromptDeSugestao(Chamado chamado) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é um assistente de suporte técnico amigável e eficiente para o gateway de pagamentos CajuPay. ");
        sb.append("Analise o histórico de um chamado de suporte e gere uma sugestão de resposta curta e direta para o técnico. ");
        sb.append("A resposta deve ser em português do Brasil e resolver o problema do cliente ou pedir mais informações, se necessário. ");
        sb.append("Não inclua saudações como 'Olá' ou assinaturas, apenas o corpo da mensagem.\n\n");
        sb.append("--- INÍCIO DO CHAMADO ---\n");
        sb.append("Categoria: ").append(chamado.getCategoria()).append("\n");
        sb.append("Título: ").append(chamado.getTitulo()).append("\n\n");
        sb.append("Histórico da Conversa:\n");

        String historico = chamado.getMensagens().stream()
                .map(msg -> msg.getAutor().getRole().name() + " (" + msg.getAutor().getNome() + "): " + msg.getTexto())
                .collect(Collectors.joining("\n"));

        sb.append(historico);
        sb.append("\n\n--- FIM DO CHAMADO ---\n\n");
        sb.append("Sugestão de resposta para o TÉCNICO:");

        return sb.toString();
    }
}

// --- Classes Auxiliares para a Resposta do Gemini (DTOs) ---
// Colocamos aqui para simplificar. Você também pode criar arquivos separados para cada uma.

@JsonIgnoreProperties(ignoreUnknown = true)
class GeminiResponse {
    private List<Candidate> candidates;
    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Candidate {
    private Content content;
    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Content {
    private List<Part> parts;
    public List<Part> getParts() { return parts; }
    public void setParts(List<Part> parts) { this.parts = parts; }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Part {
    private String text;
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}