package com.example.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class LugaresComunesApplication {

    private static final Logger logger = LoggerFactory.getLogger(LugaresComunesApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LugaresComunesApplication.class);
        
        ConfigurableApplicationContext context = app.run(args);
        Environment env = context.getEnvironment();
        
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (contextPath == null) {
            contextPath = "";
        }
        
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("The host name could not be determined, using `localhost` as fallback");
        }
        
        logger.info("""
            
            ----------------------------------------------------------
            \t🚀 Application 'Lugares Comunes API' is running! 🚀
            \t📍 Local: \t\t{}://localhost:{}{}
            \t🌐 External: \t{}://{}:{}{}
            \t📖 Profile(s): \t{}
            \t🗄️  Database: \t{}
            \t⚙️  Context: \t{}
            ----------------------------------------------------------
            """,
            protocol, serverPort, contextPath,
            protocol, hostAddress, serverPort, contextPath,
            env.getActiveProfiles().length == 0 ? "default" : String.join(", ", env.getActiveProfiles()),
            env.getProperty("spring.datasource.url"),
            contextPath
        );
    }
}