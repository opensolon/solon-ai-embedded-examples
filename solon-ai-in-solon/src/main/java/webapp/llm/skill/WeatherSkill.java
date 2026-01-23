package webapp.llm.skill;

import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.chat.ChatSession;
import org.noear.solon.ai.chat.prompt.Prompt;
import org.noear.solon.ai.chat.skill.AbsSkill;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Param;

@Component
public class WeatherSkill extends AbsSkill {
    @Inject
    WeatherService weatherService;

    @Override
    public boolean isSupported(Prompt prompt) {
        //获取原数据，做更多检测
        ChatSession session = prompt.attrAs("session");

        return prompt.getUserContent().contains("天气");
    }

    @Override
    public String getInstruction(Prompt prompt) {
        return "如果有什么天气问题，可以问我";
    }

    @ToolMapping(description = "查询天气预报")
    public String getWeather(@Param(description = "城市位置") String location) {
        return weatherService.getWeather(location);
    }
}
