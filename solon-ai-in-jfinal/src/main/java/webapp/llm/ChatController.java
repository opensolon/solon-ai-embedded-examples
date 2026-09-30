package webapp.llm;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.event.ChatEvent;
import org.noear.solon.ai.chat.event.ChatEventType;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.rx.SimpleSubscriber;

import javax.servlet.AsyncContext;

//聊天模型演示
@Path("/chat")
public class ChatController extends Controller {
    public void call(String prompt) throws Exception {
        String rst = ChatConfig.getChatModel().prompt(prompt).call()
                .getMessage()
                .getContent();

        renderText(rst, MimeType.TEXT_PLAIN_VALUE);
    }

    public void stream(String prompt) throws Exception {
        getResponse().setHeader("Content-Type", MimeType.TEXT_EVENT_STREAM_VALUE);
        AsyncContext asyncContext = getRequest().startAsync();

        ChatConfig.getChatModel().prompt(prompt).stream()
                .doOnNext(event -> {
                    try {
                        if (event.is(ChatEventType.TEXT_DELTA) && event.hasText()) {
                            getResponse().getWriter().write("data:" + event.getText());
                            getResponse().getWriter().write("\n");
                            getResponse().getWriter().flush();
                        }

                        if (event.is(ChatEventType.RESPONSE_END)) {
                            getResponse().getWriter().write("data:[DONE]"); //有些前端框架，需要 [DONE] 实识用作识别
                            getResponse().getWriter().write("\n");
                            getResponse().getWriter().flush();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        asyncContext.complete();
                    }
                }).doOnComplete(() -> {
                    asyncContext.complete();
                }).doOnError(err -> {
                    err.printStackTrace();
                    asyncContext.complete();
                }).subscribe();

    }
}