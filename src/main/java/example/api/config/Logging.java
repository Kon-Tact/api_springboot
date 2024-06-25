package example.api.config;

import example.api.controller.AccountController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class Logging {

    @Bean
    public Logger log() {
        return Logger.getLogger(AccountController.class.getName());
    }

    @Bean
    public ConsoleFormatter consoleFormat() {
        return new ConsolFormat();
    }


    @Bean
    public JSonManager jSonManager() {
        return new JSonMaker();
    }
}
