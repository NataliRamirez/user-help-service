package com.qvenly.userhelp;

import com.qvenly.userhelp.config.AuthProperties;
import com.qvenly.userhelp.config.GeminiProperties;
import com.qvenly.userhelp.config.HelpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AuthProperties.class, GeminiProperties.class, HelpProperties.class})
public class UserHelpServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserHelpServiceApplication.class, args);
    }
}
