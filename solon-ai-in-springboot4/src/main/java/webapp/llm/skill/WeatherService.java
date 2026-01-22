package webapp.llm.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WeatherService {
    private static Logger LOG = LoggerFactory.getLogger(WeatherService.class);

    public String getWeather(String location) {
        LOG.error("进来了...");

        return "晴，14度";
    }
}
