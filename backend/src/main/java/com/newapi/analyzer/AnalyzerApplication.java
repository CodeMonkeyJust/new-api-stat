package com.newapi.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class AnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String host = "localhost";

        System.out.println("\n============================================================");
        System.out.println("NewAPI Token 消耗分析工具启动成功！");
        System.out.println("============================================================");
        System.out.println("访问地址: http://" + host + ":" + port + contextPath);
        System.out.println("API文档: http://" + host + ":" + port + contextPath + "/swagger-ui.html");
        System.out.println("API文档(备用): http://" + host + ":" + port + contextPath + "/swagger-ui/index.html");
        System.out.println("API JSON: http://" + host + ":" + port + contextPath + "/v3/api-docs");
        System.out.println("============================================================\n");
    }
}
