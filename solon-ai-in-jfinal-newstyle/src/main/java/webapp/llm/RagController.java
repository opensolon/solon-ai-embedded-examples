package webapp.llm;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.util.MimeType;

import java.util.List;

@Mapping("rag")
@Controller
public class RagController {
    @Inject
    ChatModel chatModel;

    @Inject
    RepositoryStorable repository;

    @Produces(MimeType.TEXT_PLAIN_VALUE)
    @Mapping("demo")
    public String demo(String prompt) throws Exception {
        List<Document> documents = repository.search(prompt);

        ChatMessage message = ChatMessage.ofUserAugment(prompt, documents);

        return chatModel.prompt(message).call()
                .getMessage()
                .getContent();
    }
}
