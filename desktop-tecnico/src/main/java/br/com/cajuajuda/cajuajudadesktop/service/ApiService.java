package br.com.cajuajuda.cajuajudadesktop.service;

import br.com.cajuajuda.cajuajudadesktop.dto.ChamadoDto;
import br.com.cajuajuda.cajuajudadesktop.dto.DetalhesChamadoDto;
import br.com.cajuajuda.cajuajudadesktop.dto.StatusUpdateDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApiService {

    private final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // --- CORREÇÃO AQUI ---
    // A lógica de configuração do 'mapper' deve estar dentro de um construtor.
    public ApiService() {
        mapper.registerModule(new JavaTimeModule());
    }

    public String authenticate(String email, String senha) {
        try {
            String jsonBody = String.format("{\"email\":\"%s\", \"senha\":\"%s\"}", email, senha);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, String> responseMap = mapper.readValue(response.body(), Map.class);
                return responseMap.get("token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ChamadoDto> getTodosChamados(String token) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tecnico/chamados"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Optional<DetalhesChamadoDto> getDetalhesChamadoById(long id, String token) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tecnico/chamados/" + id))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Optional.of(mapper.readValue(response.body(), DetalhesChamadoDto.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean atualizarStatus(long chamadoId, StatusUpdateDto statusUpdateDto, String token) {
        try {
            String jsonBody = mapper.writeValueAsString(statusUpdateDto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tecnico/chamados/" + chamadoId + "/status"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}