package client;

import io.modelcontextprotocol.spec.McpError;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.client.McpClientProvider;
import org.noear.solon.rx.SimpleSubscriber;
import org.noear.solon.test.SolonTest;
import webapp.HelloApp;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@SolonTest(HelloApp.class)
public class McpClientTest {
    /**
     * 工具直接调用
     */
    @Test
    public void case1() throws Exception {
        McpClientProvider toolProvider = McpClientProvider.builder()
                .channel(McpChannel.STREAMABLE)
                .apiUrl("http://localhost:8080/mcp/demo1/sse")
                .build();

        //工具
        Map<String, Object> map = Collections.singletonMap("location", "杭州");
        String rst = toolProvider.callTool("getWeather", map).getContent();
        System.out.println(rst);
        assert "晴，14度".equals(rst);

        //提示语
        List<ChatMessage> messageList = toolProvider.getPrompt("askQuestion", Collections.singletonMap("topic", "demo")).getMessages();
        System.out.println(messageList);

        //资源
        String resourceContent = toolProvider.readResource("config://app-version").getContent();
        System.out.println(resourceContent);

        resourceContent = toolProvider.readResource("db://users/12/email").getContent();
        System.out.println(resourceContent);

        System.out.println("---------------");

        /// /////////////////


        toolProvider = McpClientProvider.builder()
                .channel(McpChannel.SSE)
                .apiUrl("http://localhost:8080/mcp/demo2/sse")
                .build();

        //工具
        map = Collections.singletonMap("location", "杭州");
        rst = toolProvider.callTool("getWeather", map).getContent();
        System.out.println(rst);
        assert "晴，14度".equals(rst);

        //提示语
        messageList = toolProvider.getPrompt("askQuestion", Collections.singletonMap("topic", "demo")).getMessages();
        System.out.println(messageList);

        //资源
        resourceContent = toolProvider.readResource("config://app-version").getContent();
        System.out.println(resourceContent);

        resourceContent = toolProvider.readResource("db://users/12/email").getContent();
        System.out.println(resourceContent);
    }

    //换成自己的模型配置（参考：https://solon.noear.org/article/918）
    private static final String apiUrl = "http://127.0.0.1:11434/api/chat";
    private static final String provider = "ollama";
    private static final String model = "qwen2.5:1.5b"; //"llama3.2";//deepseek-r1:1.5b;

    /**
     * 与大模型集成
     */
    @Test
    public void case2_call() throws Exception {
        McpClientProvider toolProvider = McpClientProvider.builder()
                .channel(McpChannel.STREAMABLE)
                .apiUrl("http://localhost:8080/mcp/demo1/sse")
                .build();

        ChatModel chatModel = ChatModel.of(apiUrl)
                .provider(provider)
                .model(model)
                .defaultToolsAdd(toolProvider) //添加默认工具
                .build();

        ChatResponse resp = chatModel.prompt("杭州今天的天气怎么样？")
                .call();

        System.out.println(resp.getMessage());
    }

    /**
     * 与大模型集成
     */
    @Test
    public void case2_stream() throws Exception {
        McpClientProvider toolProvider = McpClientProvider.builder()
                .channel(McpChannel.STREAMABLE)
                .apiUrl("http://localhost:8080/mcp/demo1/sse")
                .build();

        ChatModel chatModel = ChatModel.of(apiUrl)
                .provider(provider)
                .model(model)
                .defaultToolsAdd(toolProvider) //添加默认工具
                .build();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> errRef = new AtomicReference<>();

        chatModel.prompt("杭州今天的天气怎么样？")
                .stream()
                .subscribe(new SimpleSubscriber<ChatResponse>()
                        .doOnNext(resp -> {
                            System.out.println(resp.getMessage().getContent());
                        }).doOnError(err -> {
                            errRef.set(err);
                            latch.countDown();
                        }).doOnComplete(() -> {
                            latch.countDown();
                        }));

        latch.await();
        assert errRef.get() == null;
    }

    /**
     * 鉴权
     */
    @Test
    public void case3_auth() throws Exception {
        McpClientProvider mcpClient = McpClientProvider.builder()
                .channel(McpChannel.STREAMABLE)
                .apiUrl("http://localhost:8080/mcp/demo1/sse?user=no")
                .build();

        Throwable error = null;
        try {
            mcpClient.getTools();
        } catch (Throwable e) {
            error = e;
            e.printStackTrace();
        }

        assert error != null;
        assert error instanceof RuntimeException;
        assert error.getCause().getMessage().contains("401");
    }
}
