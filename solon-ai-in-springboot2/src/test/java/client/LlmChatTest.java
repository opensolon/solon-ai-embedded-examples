package client;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.HelloApp;

import java.util.concurrent.CountDownLatch;

@SolonTest(HelloApp.class)
public class LlmChatTest extends HttpTester {
    @Test
    public void call_hello() throws Exception {
        String rst = path("/chat/call/").data("prompt", "hello").post();
        System.out.println(rst);

        assert rst != null && rst.length() > 0;
    }

    @Test
    public void call_getWeather() throws Exception {
        String rst = path("/chat/call/").data("prompt", "杭州今天天气怎么样？").post();
        System.out.println(rst);

        assert rst != null && rst.length() > 0;
    }

    @Test
    public void call_getWeather_skill() throws Exception {
        String rst = path("/chat/call_skill").data("prompt", "杭州今天天气怎么样？").post();
        System.out.println(rst);

        assert rst != null && rst.length() > 0;
    }

    @Test
    public void stream_hello() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder message = new StringBuilder();

        path("/chat/stream")
                .data("prompt", "hello")
                .execAsSseStream("POST")
                .doOnNext(sse -> {
                    System.out.println(sse);
                    message.append(sse.getData());
                }).doOnComplete(() -> {
                    latch.countDown();
                })
                .doOnError(err -> {
                    err.printStackTrace();
                    latch.countDown();
                })
                .subscribe();

        latch.await();

        assert message.length() > 0;
        assert message.toString().endsWith("[DONE]");
    }

    @Test
    public void stream_getWeather() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder message = new StringBuilder();

        path("/chat/stream")
                .data("prompt", "杭州今天天气怎么样？")
                .execAsSseStream("POST")
                .doOnNext(sse -> {
                    System.out.println(sse);
                    message.append(sse.getData());
                }).doOnComplete(() -> {
                    latch.countDown();
                })
                .doOnError(err -> {
                    err.printStackTrace();
                    latch.countDown();
                })
                .subscribe();

        latch.await();

        assert message.length() > 0;
        assert message.toString().endsWith("[DONE]");
    }
}