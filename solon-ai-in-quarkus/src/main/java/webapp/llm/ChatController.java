package webapp.llm;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.noear.solon.ai.chat.ChatModel;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

//聊天模型演示
@Path("chat")
public class ChatController {
    @Inject
    ChatModel chatModel;

    @Produces(MediaType.TEXT_PLAIN)
    @Path("call")
    @GET
    public String call(String prompt) throws Exception {
        return chatModel.prompt(prompt).call()
                .getMessage()
                .getContent();
    }

    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("stream")
    @GET
    public Flux<String> stream(String prompt) throws Exception {
        return chatModel.prompt(prompt)
                .stream()
                .subscribeOn(Schedulers.boundedElastic()) //加这个打印效果更好
                .filter(resp -> resp.hasContent())
                .map(resp -> resp.getContent())
                .concatWithValues("[DONE]"); //有些前端框架，需要 [DONE] 实识用作识别

    }
}