package webapp.llm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.noear.solon.ai.chat.ChatModel;
import webapp.llm.tool.CalculatorTools;
import webapp.llm.tool.WeatherTools;

//配置聊天模型
@ApplicationScoped
public class ChatConfig {
    /**
     * 与大模型集成
     */
    @Produces
    public ChatModel chatModel(CalculatorTools calculatorTools) throws Exception {
        return ChatModel.of(_Constants.chat_apiUrl)
                .provider(_Constants.chat_provider)
                .model(_Constants.chat_model)
                .defaultToolAdd(new WeatherTools()) //添加默认工具
                .defaultToolAdd(calculatorTools) //如果是托管组件，使用注入实例（不要手动 new）
                .build();
    }
}
