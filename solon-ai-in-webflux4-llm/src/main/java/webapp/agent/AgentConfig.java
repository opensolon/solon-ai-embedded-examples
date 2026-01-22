package webapp.agent;

import org.noear.solon.ai.agent.AgentSession;
import org.noear.solon.ai.agent.AgentSessionProvider;
import org.noear.solon.ai.agent.react.ReActAgent;
import org.noear.solon.ai.agent.session.InMemoryAgentSession;
import org.noear.solon.ai.chat.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class AgentConfig {

    @Bean
    public ReActAgent getAgent(ChatModel chatModel) {
        return ReActAgent.of(chatModel)
                .build();
    }

    @Bean
    public AgentSessionProvider getSessionProvider() {
        Map<String, AgentSession> map = new LinkedHashMap<>();
        return (sessionId) -> map.computeIfAbsent(sessionId,
                k -> InMemoryAgentSession.of(k));
    }
}
