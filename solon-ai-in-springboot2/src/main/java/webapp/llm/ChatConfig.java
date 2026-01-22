package webapp.llm;

import org.noear.solon.ai.chat.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import webapp.llm.skill.WeatherSkill;
import webapp.llm.tool.CalculatorTools;
import webapp.llm.tool.WeatherTools;

//配置聊天模型
@Configuration
public class ChatConfig {
    /**
     * 与大模型集成
     */
    @Bean
    public ChatModel chatModel(CalculatorTools calculatorTools) throws Exception {
        return ChatModel.of(_Constants.chat_apiUrl)
                .provider(_Constants.chat_provider)
                .model(_Constants.chat_model)
                .defaultToolsAdd(new WeatherTools()) //添加默认工具
                .defaultToolsAdd(calculatorTools) //如果是托管组件，使用注入实例（不要手动 new）
                .build();
    }

    @Bean("chatModelForSkill")
    public ChatModel chatModelForSkill(WeatherSkill weatherSkill) {
        return ChatModel.of(_Constants.chat_apiUrl)
                .provider(_Constants.chat_provider)
                .model(_Constants.chat_model)
                .defaultSkillAdd(weatherSkill)
                .build();
    }
}
