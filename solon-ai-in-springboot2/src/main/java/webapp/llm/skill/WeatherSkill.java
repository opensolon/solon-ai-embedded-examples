package webapp.llm.skill;

import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.chat.ChatSession;
import org.noear.solon.ai.chat.prompt.ChatPrompt;
import org.noear.solon.ai.chat.skill.AbsSkill;
import org.noear.solon.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeatherSkill extends AbsSkill {
    @Autowired
    WeatherService weatherService;

    @Override
    public boolean isSupported(ChatPrompt prompt) {
        //获取原数据，做更多检测
        ChatSession session = prompt.getMetaAs("session");

        return prompt.getUserMessageContent().contains("天气");
    }

    @Override
    public String getInstruction(ChatPrompt prompt) {
        return "如果有什么天气问题，可以问我";
    }

    @ToolMapping(description = "查询天气预报")
    public String getWeather(@Param(description = "城市位置") String location) {
        return weatherService.getWeather(location);
    }
}
