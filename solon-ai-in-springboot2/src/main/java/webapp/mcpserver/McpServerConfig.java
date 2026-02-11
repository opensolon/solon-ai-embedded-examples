package webapp.mcpserver;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.ai.chat.tool.MethodToolProvider;
import org.noear.solon.ai.chat.tool.ToolSchemaUtil;
import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.server.IMcpServerEndpoint;
import org.noear.solon.ai.mcp.server.McpServerEndpointProvider;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.ai.chat.prompt.MethodPromptProvider;
import org.noear.solon.ai.chat.resource.MethodResourceProvider;
import org.noear.solon.ai.util.ParamDesc;
import org.noear.solon.web.servlet.SolonServletFilter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import webapp.mcpserver.tool.McpServerTool2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 这个类独立一个目录，可以让 Solon 扫描范围最小化
 * */
@Configuration
public class McpServerConfig {
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Autowired
    private List<IMcpServerEndpoint> serverEndpoints;

    @PostConstruct
    public void start() {
        /**
         * Spring 注解支持
         * */

        ToolSchemaUtil.addBodyDetector(e -> e.isAnnotationPresent(RequestBody.class));
        ToolSchemaUtil.addParamResolver((e,t)->{
            RequestParam p1Anno = e.getAnnotation(RequestParam.class);

            if (p1Anno != null) { //这个注解因为没有描述字段，所以变量名一定要很语义
                Parameter p1 = (Parameter) e;
                String name = Utils.annoAlias(p1Anno.name(), p1.getName());
                return new ParamDesc(name, t.getGenericType(), p1Anno.required(), "");
            }

            return null;
        });

        ///

        System.setProperty("server.contextPath", contextPath);

        Solon.start(McpServerConfig.class, new String[]{"--cfg=mcpserver.yml"}, app -> {
            //添加全局鉴权过滤器示例（如果不需要，可以删掉）
            app.enableScanning(false); //不扫描
            app.filter(new McpServerAuth());
        });

        /**
         * 手动构建端点示例（仅供参考）
         * */

        //手动构建 mcp 服务端点（只是演示，可以去掉）
        McpServerEndpointProvider endpointProvider = McpServerEndpointProvider.builder()
                .name("McpServerTool2")
                .channel(McpChannel.SSE)
                .sseEndpoint("/mcp/demo2/sse")
                .build();
        endpointProvider.addTool(new MethodToolProvider(new McpServerTool2()));
        endpointProvider.addResource(new MethodResourceProvider(new McpServerTool2()));
        endpointProvider.addPrompt(new MethodPromptProvider(new McpServerTool2()));
        endpointProvider.postStart();

        //手动加入到 solon 容器（只是演示，可以去掉）
        Solon.context().wrapAndPut(endpointProvider.getName(), endpointProvider);

        /**
         * Spring 组件转为端点
         * */

        springCom2Endpoint();
    }

    @PreDestroy
    public void stop() {
        if (Solon.app() != null) {
            //停止 solon（根据配置，可支持两段式安全停止）
            Solon.stopBlock(false, Solon.cfg().stopDelay());
        }
    }

    //Spring 组件转为端点
    protected void springCom2Endpoint() {
        //提取实现容器里 IMcpServerEndpoint 接口的 bean ，并注册为服务端点
        for (IMcpServerEndpoint serverEndpoint : serverEndpoints) {
            Class<?> serverEndpointClz = AopUtils.getTargetClass(serverEndpoint);
            McpServerEndpoint anno = AnnotationUtils.findAnnotation(serverEndpointClz, McpServerEndpoint.class);

            if (anno == null) {
                continue;
            }

            McpServerEndpointProvider serverEndpointProvider = McpServerEndpointProvider.builder()
                    .from(serverEndpointClz, anno)
                    .build();

            serverEndpointProvider.addTool(new MethodToolProvider(serverEndpointClz, serverEndpoint));
            serverEndpointProvider.addResource(new MethodResourceProvider(serverEndpointClz, serverEndpoint));
            serverEndpointProvider.addPrompt(new MethodPromptProvider(serverEndpointClz, serverEndpoint));

            serverEndpointProvider.postStart();

            //可以再把 serverEndpointProvider 手动转入 SpringBoot 容器
        }
    }

    @Bean
    public FilterRegistrationBean mcpServerFilter() {
        //通过 Servlet Filter 实现 http 能力对接
        FilterRegistrationBean<SolonServletFilter> filter = new FilterRegistrationBean<>();
        filter.setName("SolonFilter");
        filter.addUrlPatterns("/mcp/*");
        filter.setFilter(new SolonServletFilter());
        return filter;
    }
}