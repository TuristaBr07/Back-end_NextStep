package com.nextstep.backend.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextstep.backend.dtos.ChatRequestDTO;
import com.nextstep.backend.dtos.ChatResponseDTO;
import com.nextstep.backend.dtos.TransacaoDTO;
import com.nextstep.backend.exceptions.RegraNegocioException;
import com.nextstep.backend.models.Usuario;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final OpenAiService openAiService;
    private final TransacaoService transacaoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatbotServiceImpl(OpenAiService openAiService, TransacaoService transacaoService) {
        this.openAiService = openAiService;
        this.transacaoService = transacaoService;
    }

    @Override
    public ChatResponseDTO conversar(ChatRequestDTO request, Usuario usuario) {
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            throw new RegraNegocioException("Envie uma mensagem para o chatbot.");
        }

        String jsonRespostaIA = openAiService.processarMensagem(request.getMessage());

        try {
            JsonNode iaNode = objectMapper.readTree(jsonRespostaIA);
            String intent = iaNode.path("intent").asText();
            String reply = iaNode.path("reply").asText("Operação processada.");

            if ("REGISTRAR".equalsIgnoreCase(intent)) {
                TransacaoDTO dto = new TransacaoDTO(
                        iaNode.path("date").asText(LocalDate.now().toString()),
                        iaNode.path("type").asText(),
                        iaNode.path("category").asText(),
                        iaNode.path("description").asText(),
                        iaNode.path("amount").asDouble()
                );

                transacaoService.salvar(dto, usuario);
            }

            return new ChatResponseDTO(reply);
        } catch (Exception e) {
            return new ChatResponseDTO("Desculpe, entendi sua mensagem, mas não consegui estruturar os dados para salvar.");
        }
    }
}