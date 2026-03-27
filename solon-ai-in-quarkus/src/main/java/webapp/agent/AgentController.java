package webapp.agent;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.noear.solon.ai.agent.AgentSessionProvider;
import org.noear.solon.ai.agent.react.ReActAgent;

@Path("/agent")
public class AgentController {
    @Inject
    ReActAgent agent;

    @Inject
    AgentSessionProvider sessionProvider;

    @Produces(MediaType.TEXT_PLAIN)
    @Path("call")
    @GET
    public String call(String sessionId, String prompt) throws Throwable {
        return agent.prompt(prompt)
                .session(sessionProvider.getSession(sessionId))
                .call()
                .getContent();
    }
}
