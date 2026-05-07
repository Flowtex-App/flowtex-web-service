package com.flowtex.FormBuilder.Infrastructure.Ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowtex.FormBuilder.Application.Internal.OutboundServices.FieldSuggestionService;
import com.flowtex.FormBuilder.Domain.Model.Commands.SuggestFieldNamesCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Primary
public class GroqFieldSuggestionService implements FieldSuggestionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroqFieldSuggestionService.class);

    private final String apiUrl;
    private final String apiKey;
    private final String model;
    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public GroqFieldSuggestionService(
            @Value("${flowtex.ai.groq.api-url}") String apiUrl,
            @Value("${flowtex.ai.groq.api-key}") String apiKey,
            @Value("${flowtex.ai.groq.model}") String model) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = WebClient.builder().build();
    }

    @Override
    public List<FieldSuggestion> suggest(SuggestFieldNamesCommand command) {
        if (apiKey == null || apiKey.isBlank()) {
            LOGGER.warn("GROQ_API_KEY no configurada. Devolviendo sugerencias heuristicas locales.");
            return localFallback(command);
        }

        String systemPrompt = """
                Eres un asistente experto en diseno de formularios para empresas peruanas.
                Recibiras el contexto y el titulo de un formulario, y debes proponer una lista de campos relevantes.
                Responde EXCLUSIVAMENTE con un JSON valido sin texto adicional, con la forma:
                {"suggestions":[{"label":"...","fieldKey":"snake_case","fieldType":"TEXT|TEXTAREA|NUMBER|EMAIL|DATE|DATETIME|SELECT|MULTI_SELECT|RADIO|CHECKBOX|FILE|URL|PHONE|SIGNATURE","rationale":"..."}]}
                Reglas:
                - fieldKey en snake_case sin acentos.
                - label en espanol claro y profesional.
                - fieldType debe ser uno de los enumerados.
                - Genera entre 5 y 8 campos.
                """;

        String userPrompt = String.format("Titulo: %s%nContexto: %s%nNumero maximo de sugerencias: %d",
                command.formTitle() != null ? command.formTitle() : "(sin titulo)",
                command.formContext() != null ? command.formContext() : "(sin contexto)",
                command.maxSuggestions() != null ? command.maxSuggestions() : 6);

        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.3,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)));

        try {
            String response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(20))
                    .block();

            return parseResponse(response);
        } catch (Exception ex) {
            LOGGER.error("Error consultando Groq: {}. Usando fallback local.", ex.getMessage());
            return localFallback(command);
        }
    }

    private List<FieldSuggestion> parseResponse(String rawJson) throws Exception {
        JsonNode root = mapper.readTree(rawJson);
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        if (contentNode.isMissingNode() || contentNode.isNull()) {
            return List.of();
        }
        JsonNode parsed = mapper.readTree(contentNode.asText());
        JsonNode suggestionsNode = parsed.path("suggestions");

        List<FieldSuggestion> result = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            for (JsonNode node : suggestionsNode) {
                result.add(new FieldSuggestion(
                        node.path("label").asText(""),
                        node.path("fieldKey").asText(""),
                        node.path("fieldType").asText("TEXT"),
                        node.path("rationale").asText("")));
            }
        }
        return result;
    }

    private List<FieldSuggestion> localFallback(SuggestFieldNamesCommand command) {
        String title = command.formTitle() != null ? command.formTitle().toLowerCase() : "";
        String ctx = command.formContext() != null ? command.formContext().toLowerCase() : "";

        List<FieldSuggestion> suggestions = new ArrayList<>();
        suggestions.add(new FieldSuggestion("Nombre completo", "full_name", "TEXT", "Identificacion del solicitante."));
        suggestions.add(new FieldSuggestion("Correo corporativo", "corporate_email", "EMAIL", "Notificacion del estado del flujo."));
        suggestions.add(new FieldSuggestion("Area solicitante", "requesting_area", "SELECT", "Para enrutar la aprobacion."));
        suggestions.add(new FieldSuggestion("Descripcion", "description", "TEXTAREA", "Detalle del pedido."));
        suggestions.add(new FieldSuggestion("Fecha requerida", "required_date", "DATE", "Cuando se requiere el resultado."));

        if (title.contains("compra") || ctx.contains("compra")) {
            suggestions.add(new FieldSuggestion("Monto estimado", "estimated_amount", "NUMBER", "Para enrutar por monto."));
            suggestions.add(new FieldSuggestion("Cotizacion adjunta", "quotation_file", "FILE", "Soporte de la compra."));
        }
        if (title.contains("incidente") || ctx.contains("seguridad")) {
            suggestions.add(new FieldSuggestion("Severidad", "severity", "SELECT", "Clasificacion ISO 27001."));
            suggestions.add(new FieldSuggestion("Evidencias", "evidence", "FILE", "Capturas o logs del incidente."));
        }
        if (title.contains("vacacion") || ctx.contains("rrhh")) {
            suggestions.add(new FieldSuggestion("Persona de respaldo", "backup_person", "TEXT", "Quien atendera pendientes."));
        }
        return suggestions;
    }
}
