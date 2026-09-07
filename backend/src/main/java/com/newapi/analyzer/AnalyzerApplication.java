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
        String base = "http://localhost:" + port + contextPath;
        // springdoc.api-docs.enabled 的取值来自 application.yml 中的 ${SWAGGER_ENABLED:false}
        boolean swaggerEnabled = env.getProperty("springdoc.api-docs.enabled", Boolean.class, false);

        System.out.println("\n============================================================");
        System.out.println("NewAPI Token 消耗分析工具启动成功！");
        System.out.println("============================================================");
        System.out.println("健康检查: " + base + "/actuator/health");
        if (swaggerEnabled) {
            System.out.println("API文档: " + base + "/swagger-ui.html");
            System.out.println("API文档(备用): " + base + "/swagger-ui/index.html");
            System.out.println("API JSON: " + base + "/v3/api-docs");
        } else {
            System.out.println("API 文档未启用：设置环境变量 SWAGGER_ENABLED=true 并重启后，即可访问 Swagger UI");
        }
        System.out.println("============================================================");
        System.out.println("提示：若从其它机器访问，请把 localhost 换成服务器地址（如 http://<服务器IP>:" + port + contextPath + "）\n");
    }
}
