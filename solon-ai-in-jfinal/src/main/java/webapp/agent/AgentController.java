package webapp.agent;

import com.jfinal.core.Controller;
import com.jfinal.core.Path;


@Path("/agent")
public class AgentController extends Controller {
    public String call(String sessionId, String prompt) throws Throwable {
        return AgentConfig.getAgent()
                .prompt(prompt)
                .session(AgentConfig.getSessionProvider().getSession(sessionId))
                .call()
                .getContent();
    }
}
