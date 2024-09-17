package pl.mojezapiski.ai.ollama;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OllamaRestController {
    private final OllamaChatModel ollamaChatClient;
    @PostMapping("/ollama")
    String ollama(@RequestBody UserRequest request) {
        var promptAi = new org.springframework.ai.chat.prompt.Prompt(
                request.message(),
                OllamaOptions.create().withModel(request.model() == null ? "llama2-uncensored" : request.model())
        );
        var response =  ollamaChatClient.call(promptAi);
        return response.getResult().getOutput().getContent();
    }
}
