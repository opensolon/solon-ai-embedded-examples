package webapp.llm;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatSession;
import org.noear.solon.ai.chat.prompt.Prompt;
import org.noear.solon.ai.chat.session.InMemoryChatSession;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

//聊天模型演示
@Mapping("chat")
@Controller
public class ChatController {
    @Inject
    ChatModel chatModel;

    @Inject("chatModelForSkill")
    ChatModel chatModelForSkill;


    @Produces(MimeType.TEXT_PLAIN_VALUE)
    @Mapping("call_skill")
    public String call_skill(String query) throws Exception {
        ChatSession session = InMemoryChatSession.builder().build();

        //提示词元数据的应用
        Prompt prompt = Prompt.of(query).attrPut("session", session);

        return chatModelForSkill.prompt(prompt).call()
                .getMessage()
                .getContent();
    }

    @Produces(MimeType.TEXT_PLAIN_VALUE)
    @Mapping("call")
    public String call(String prompt) throws Exception {
        return chatModel.prompt(prompt).call()
                .getMessage()
                .getContent();
    }

    @Produces(MimeType.TEXT_EVENT_STREAM_VALUE)
    @Mapping("stream")
    public Flux<String> stream(String prompt) throws Exception {
        return chatModel.prompt(prompt)
                .stream()
                .subscribeOn(Schedulers.boundedElastic()) //加这个打印效果更好
                .filter(resp -> resp.hasContent())
                .map(resp -> resp.getContent())
                .concatWithValues("[DONE]"); //有些前端框架，需要 [DONE] 实识用作识别
    }
}