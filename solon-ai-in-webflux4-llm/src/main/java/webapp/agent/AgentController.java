package webapp.agent;

import org.noear.solon.ai.agent.AgentSessionProvider;
import org.noear.solon.ai.agent.react.ReActAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("agent")
@RestController
public class AgentController {
    @Autowired
    ReActAgent agent;

    @Autowired
    AgentSessionProvider sessionProvider;

    @RequestMapping("call")
    public String call(String sessionId, String query) throws Throwable {
        return agent.prompt(query)
                .session(sessionProvider.getSession(sessionId))
                .call()
                .getContent();
    }
}
