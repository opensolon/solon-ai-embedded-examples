package webapp.llm;

import org.noear.solon.ai.chat.ChatModel;
import webapp.llm.tool.WeatherTools;

//配置聊天模型
public class ChatConfig {
    private static ChatModel chatModel;

    /**
     * 与大模型集成
     */
    public static ChatModel getChatModel() {
        if (chatModel == null) {
            chatModel = ChatModel.of(_Constants.chat_apiUrl)
                    .provider(_Constants.chat_provider)
                    .model(_Constants.chat_model)
                    .defaultToolAdd(new WeatherTools()) //添加默认工具
                    .build();
        }

        return chatModel;
    }
}
