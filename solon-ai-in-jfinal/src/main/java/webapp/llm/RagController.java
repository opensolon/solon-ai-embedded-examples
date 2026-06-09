package webapp.llm;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.core.util.MimeType;

import java.util.List;

@Path("/rag")
public class RagController extends Controller {
    public void demo(String prompt) throws Exception {
        List<Document> documents = RagConfig.getRepository().search(prompt);

        ChatMessage message = ChatMessage.ofUserAugment(prompt, documents);

        String rst = ChatConfig.getChatModel()
                .prompt(message)
                .call()
                .getMessage()
                .getContent();

        renderText(rst, MimeType.TEXT_PLAIN_VALUE);
    }
}