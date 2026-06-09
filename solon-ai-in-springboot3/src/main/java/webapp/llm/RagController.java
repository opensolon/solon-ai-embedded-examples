package webapp.llm;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.annotation.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("rag")
@RestController
public class RagController {
    @Autowired
    ChatModel chatModel;

    @Autowired
    RepositoryStorable repository;

    @Produces(MediaType.TEXT_PLAIN_VALUE)
    @RequestMapping("demo")
    public String demo(String prompt) throws Exception {
        List<Document> documents = repository.search(prompt);

        ChatMessage message = ChatMessage.ofUserAugment(prompt, documents);

        return chatModel.prompt(message).call()
                .getMessage()
                .getContent();
    }
}
