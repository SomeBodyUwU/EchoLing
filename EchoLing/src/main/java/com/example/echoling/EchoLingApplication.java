package com.example.echoling;

import com.example.echoling.auntefication.SessionDataSaver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EchoLingApplication {

    public static void main(String[] args) {
        SpringApplication.run(EchoLingApplication.class, args);
    }
    @Bean
    public SessionDataSaver sessionDataSaver (){
        return new SessionDataSaver();
    }
}
