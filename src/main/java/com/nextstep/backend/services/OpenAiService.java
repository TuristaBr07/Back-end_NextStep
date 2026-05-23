package com.nextstep.backend.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAiService {

    @Value("${openai.api.key:}" )
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String processarMensagem(String mensagemUsuario) {
        if (apiKey == null || apiKey.isBlank()) {
            return "{\"intent\": \"ERRO\", \"reply\": \"A chave da OpenAI não foi configurada no servidor.\"}";
        }

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders( );
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt = """
                Você é o assistente financeiro do app NextStep.
                O usuário pode informar uma transação ou fazer uma pergunta.
                Sua tarefa é retornar estritamente um JSON válido, sem markdown, sem crases e sem texto fora do JSON.
                Use a estrutura abaixo:
                {
                  "intent": "REGISTRAR" ou "CONVERSAR",
                  "date": "yyyy-MM-dd",
                  "type": "Despesa" ou "Receita",
                  "amount": 0.0,
                  "category": "nome da categoria",
                  "description": "descrição da transação",
                  "reply": "resposta humana curta confirmando a ação ou respondendo à dúvida"
                }
                Se não houver transação para registrar, use intent CONVERSAR.
                """;

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini");
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", mensagemUsuario)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            String conteudo = root.path("choices").path(0).path("message").path("content").asText();
            return conteudo.replace("```json", "").replace("```", "").trim();
        } catch (Exception e) {
            return "{\"intent\": \"ERRO\", \"reply\": \"Desculpe, tive um problema de conexão com o serviço de inteligência artificial.\"}";
        }
    }
}