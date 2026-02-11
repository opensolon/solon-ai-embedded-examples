package client;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.client.McpClientProvider;
import org.noear.solon.ai.mcp.client.McpServerParameters;

import java.util.Collections;


public class McpStdioDemo {
    public static void test() {
        //服务端不能开启控制台的日志，不然会污染协议流
        McpClientProvider mcpClient = McpClientProvider.builder()
                .channel(McpChannel.STDIO) //表示使用 stdio
                .serverParameters(McpServerParameters.builder("npx")
                        .args("/c", "npx.cmd", "-y", "@modelcontextprotocol/server-everything", "dir")
                        .build())
                .build();

        //随便写的，示意一下
        String response = mcpClient.callTool("demo", Collections.singletonMap("p1", "test"))
                .getContent();

        assert response != null;
        System.out.println(response);

        mcpClient.close();
    }

    public void demo(McpClientProvider toolProvider) throws Exception {
        ChatModel chatModel = ChatModel.of("...")
                .defaultToolsAdd(toolProvider) //添加默认工具
                .build();

        chatModel.prompt("杭州今天的天气怎么样？")
                .call();
    }
}
