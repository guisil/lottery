package exercises.lottery.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for the application.
 *
 * Created by guisil on 24/07/2016.
 */
@Configuration
@Profile(value = {"main", "integration"})
@PropertySource(value = "classpath:application.properties")
public class LotteryConfiguration {

    @Value("${defaultNumberOfLines}")
    private int defaultNumberOfLines;
    @Bean
    @Qualifier("defaultNumberOfLines")
    public int getDefaultNumberOfLines() {
        return defaultNumberOfLines;
    }
}
