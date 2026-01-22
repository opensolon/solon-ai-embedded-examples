package webapp.llm;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatSession;
import org.noear.solon.ai.chat.prompt.Prompt;
import org.noear.solon.ai.chat.session.InMemoryChatSession;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.util.MimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

//聊天模型演示
@RequestMapping("chat")
@RestController
public class ChatController {
    @Autowired
    ChatModel chatModel;

    @Autowired
    @Qualifier("chatModelForSkill")
    ChatModel chatModelForSkill;

    @Produces(MimeType.TEXT_PLAIN_VALUE)
    @RequestMapping("call_skill")
    public String call_skill(String query) throws Exception {
        ChatSession session = InMemoryChatSession.builder().build();

        //提示词元数据的应用
        Prompt prompt = Prompt.of(query).metaPut("session", session);

        return chatModelForSkill.prompt(prompt).call()
                .getMessage()
                .getContent();
    }

    @RequestMapping(value = "call", produces = MediaType.TEXT_PLAIN_VALUE)
    public String call(String prompt) throws Exception {
        return chatModel.prompt(prompt).call()
                .getMessage()
                .getContent();
    }

    @RequestMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(String prompt) throws Exception {
        return Flux.from(chatModel.prompt(prompt).stream())
                .subscribeOn(Schedulers.boundedElastic()) //加这个打印效果更好
                .limitRate(Integer.MAX_VALUE)
                .filter(resp -> resp.hasContent())
                .map(resp -> resp.getContent())
                .concatWithValues("[DONE]"); //有些前端框架，需要 [DONE] 实识用作识别
    }
}