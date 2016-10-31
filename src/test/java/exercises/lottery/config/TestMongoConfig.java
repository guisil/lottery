package exercises.lottery.config;

import com.mongodb.Mongo;
import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * Configuration class for the database tests.
 *
 * Created by guisil on 25/07/2016.
 */
@Profile("testing")
@Configuration
@PropertySource(value = "classpath:application.testing.properties")
public class TestMongoConfig extends AbstractMongoConfiguration {

    @Autowired
    private Environment environment;

    private int databasePort() {
        return Integer.parseInt(environment.getRequiredProperty("mongo.db.port"));
    }

    @Override
    protected String getDatabaseName() {
        return environment.getRequiredProperty("mongo.db.name");
    }

    @Override
    public Mongo mongo() throws Exception {
        return new EmbeddedMongoBuilder()
                .version(Version.V3_2_1)
                .bindIp("127.0.0.1")
                .port(databasePort())
                .build();
    }
}
