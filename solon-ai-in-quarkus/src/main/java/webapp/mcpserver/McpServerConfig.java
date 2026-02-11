package webapp.mcpserver;

import io.quarkus.runtime.Startup;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import org.noear.solon.Solon;
import org.noear.solon.ai.chat.tool.MethodToolProvider;
import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.server.IMcpServerEndpoint;
import org.noear.solon.ai.mcp.server.McpServerEndpointProvider;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.ai.chat.prompt.MethodPromptProvider;
import org.noear.solon.ai.chat.resource.MethodResourceProvider;
import webapp.mcpserver.handle.QuarkusHandler;
import webapp.mcpserver.tool.McpServerTool2;

import java.util.Set;

/**
 * 这个类独立一个目录，可以让 Solon 扫描范围最小化
 * */
@Startup
@ApplicationScoped
public class McpServerConfig extends AbstractVerticle  {

    @Inject
    Router router;

    @Inject
    @Any
    Instance<IMcpServerEndpoint> serverEndpoints;


    @Inject
    QuarkusHandler handler;

    @Inject
    BeanManager beanManager;  // 注入BeanManager

    @Produces
    @ApplicationScoped
    public QuarkusHandler handler() {
        System.out.println("=== QuarkusHandler ===");
        return new QuarkusHandler();
    }


    @PostConstruct
    @Override
    public void start() {
        System.out.println("McpServerConfig.start");
        router.routeWithRegex("/mcp/.*").handler(req -> {
            handler.handle(req.request());
        });

        Solon.start(McpServerConfig.class, new String[]{"--cfg=mcpserver.yml"}, app->{
            //添加全局鉴权过滤器示例（如果不需要，可以删掉）
            app.enableScanning(false); //不扫描
            app.filter(new McpServerAuth());
        });

        //手动构建 mcp 服务端点（只是演示，可以去掉） -- 目前该模块未打通 quarkus 框架上运行的案例，请使用 quarkusCom2Endpoint() 的方式构建即可
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
         * quarkus 组件转为端点
         * */

        quarkusCom2Endpoint();
    }

    @Override
    public void stop() {
        if (Solon.app() != null) {
            Solon.stopBlock(false, Solon.cfg().stopDelay());
        }
    }

    // quarkus 组件转为端点
    protected void quarkusCom2Endpoint() {
        //提取实现容器里 IMcpServerEndpoint 接口的 bean ，并注册为服务端点
        if (serverEndpoints!=null){
            Set<Bean<?>> candidateBeans = beanManager.getBeans(IMcpServerEndpoint.class);
            if (candidateBeans.isEmpty()) {
                return;
            }
            for (Bean<?> serverEndpoint : candidateBeans) {//
//                System.out.println("bean->"+serverEndpoint.getBeanClass().getName());
//                System.out.println("name->"+serverEndpoint.getClass().getName());
                // 2. 获取Bean的原始类（被代理的实际类）
                Class<?> serverEndpointClz = serverEndpoint.getBeanClass();
//                Class<?> serverEndpointClz = Class.forName(serverEndpoint.getBeanClass().getName(), false, Thread.currentThread().getContextClassLoader());
                McpServerEndpoint anno = serverEndpointClz.getAnnotation(McpServerEndpoint.class);
                if (anno == null) {
                    continue;
                }

                // quarkus 对象 -- 这个其实获取代理类
                CreationalContext<?> ctx = beanManager.createCreationalContext(serverEndpoint);
                // 获取实例本身  -- 其实就是 serverEndpoints 里的代理对象，反射方法必须由这里触发
                Object instance = beanManager.getReference(
                        serverEndpoint,
                        serverEndpointClz,
                        ctx
                );
                McpServerEndpointProvider serverEndpointProvider = McpServerEndpointProvider.builder()
                        .from(serverEndpointClz, anno)
                        .build();
                serverEndpointProvider.addTool(new MethodToolProvider(serverEndpointClz, instance));
                serverEndpointProvider.addResource(new MethodResourceProvider(serverEndpointClz, instance));
                serverEndpointProvider.addPrompt(new MethodPromptProvider(serverEndpointClz, instance));
                serverEndpointProvider.postStart();
                // 再注入 solon 端点 -- 这个可以不需要，服务可以正常响应了
//                Solon.context().wrapAndPut(serverEndpointProvider.getName(), serverEndpointProvider);

                //可以再把 serverEndpointProvider 手动转入 SpringBoot 容器
            }
        }

    }
}
