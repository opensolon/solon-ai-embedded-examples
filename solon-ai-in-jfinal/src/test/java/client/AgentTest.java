package client;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.HelloApp;

@SolonTest(HelloApp.class)
public class AgentTest extends HttpTester {
    @Test
    public void call_hello() throws Exception {
        String rst = path("/agent/call")
                .data("sessionId", "1")
                .data("prompt", "hello").post();
        System.out.println(rst);

        assert rst != null && rst.length() > 0;
    }
}
