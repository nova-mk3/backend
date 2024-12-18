package org.nova.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        System.setProperty("SPRING_PROFILE_ACTIVE", dotenv.get("SPRING_PROFILE_ACTIVE"));
        SpringApplication.run(BackendApplication.class, args);
    }


}
