package webapp;

import org.noear.solon.Solon;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class HelloApp {
    public static void main(String[] args) {
        if(Solon.app() != null) {
            return;
        }

        SpringApplication.run(HelloApp.class, args);
    }

    @RequestMapping("/")
    public String hello(String name) {
//        //动态获取（添加工具等）//只是示例，可以删掉
//        McpServerEndpointProvider serverEndpointProvider = Solon.context().getBean("demo1");
//
//        serverEndpointProvider.addTool(new FunctionToolDesc(null));
//        serverEndpointProvider.addResource(new FunctionResourceDesc(null));
//        serverEndpointProvider.addPrompt(new FunctionPromptDesc(null));

        return "hello world: " + name;
    }

    @RequestMapping("/hello2")
    public String hello2(String name) throws Exception {
        Thread.sleep(10);
        return "hello world: " + name;
    }
}