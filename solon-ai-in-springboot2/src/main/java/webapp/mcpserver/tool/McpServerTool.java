package webapp.mcpserver.tool;

import org.noear.solon.ai.annotation.PromptMapping;
import org.noear.solon.ai.annotation.ResourceMapping;
import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.server.IMcpServerEndpoint;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

/**
 * 自动构建服务端点服务（使用 springboot 容器） //通过 IMcpServerEndpoint 接口，方便自动收集 McpServerEndpoint 组件类
 *
 * STREAMABLE_STATELESS，集群时不需要 ip_hash 路由，但不支持向 client 发送变更通知
 * STREAMABLE 或 SSE，集群时需要 ip_hash 路由
 * */
@Service
@McpServerEndpoint(channel = McpChannel.STREAMABLE, name="demo1", mcpEndpoint = "/mcp/demo1/sse")
public class McpServerTool implements IMcpServerEndpoint {
    @Autowired //示例注入 spring bean
    DemoService demoService;

    //
    // 建议开启编译参数：-parameters （否则，最好再配置参数的 name）
    //
    @ToolMapping(description = "查询天气预报")
    public String getWeather(@Param(description = "城市位置") String location) {
        return "晴，14度";
    }

    @ResourceMapping(uri = "config://app-version", description = "获取应用版本号")
    public String getAppVersion() {
        return "v3.2.0";
    }

    @ResourceMapping(uri = "db://users/{user_id}/email", description = "根据用户ID查询邮箱")
    public String getEmail(@Param(description = "用户Id") String user_id) {
        return user_id + "@example.com";
    }

    @PromptMapping(description = "生成关于某个主题的提问")
    public Collection<ChatMessage> askQuestion(@Param(description = "主题") String topic) {
        return Arrays.asList(
                ChatMessage.ofUser("请解释一下'" + topic + "'的概念？")
        );
    }
}