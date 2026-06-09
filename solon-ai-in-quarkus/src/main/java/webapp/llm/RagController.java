package webapp.llm;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;

import java.util.List;

@Path("rag")
public class RagController {
    @Inject
    ChatModel chatModel;

    @Inject
    RepositoryStorable repository;

    @Produces(MediaType.TEXT_PLAIN)
    @Path("demo")
    @GET
    public String demo(String prompt) throws Exception {
        List<Document> documents = repository.search(prompt);

        ChatMessage message = ChatMessage.ofUserAugment(prompt, documents);

        return chatModel.prompt(message).call()
                .getMessage()
                .getContent();
    }
}
