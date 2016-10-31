package exercises.lottery.config;

import exercises.lottery.resources.LotteryResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * Configuration class for Jersey.
 *
 * Created by guisil on 24/07/2016.
 */
@Profile(value = {"main", "integration"})
@Component
@ApplicationPath("/lottery")
public class JerseyConfig  extends ResourceConfig {

    public JerseyConfig() {
        register(LotteryResource.class);
    }
}
