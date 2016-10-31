package exercises.lottery;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application class.
 *
 * Created by guisil on 24/07/2016.
 */
@ComponentScan
@SpringBootApplication
public class LotteryApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        new LotteryApplication()
                .configure(new SpringApplicationBuilder(LotteryApplication.class))
                .run(args);
    }
}
