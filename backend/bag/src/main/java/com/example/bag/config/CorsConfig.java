package com.example.bag.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${frontend.url:}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> originPatterns = new ArrayList<>();
        originPatterns.add("https://*.onrender.com*");
        originPatterns.add("https://*.vercel.app*");
        originPatterns.add("http://localhost:*");
        originPatterns.add("http://127.0.0.1:*");

        if (frontendUrl != null && !frontendUrl.trim().isEmpty()) {
            String cleanUrl = frontendUrl.trim();
            if (cleanUrl.endsWith("/")) {
                cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
            }
            originPatterns.add(cleanUrl);
            originPatterns.add(cleanUrl + "*");
        }

        registry.addMapping("/**")
                .allowedOriginPatterns(originPatterns.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type", "X-Total-Count")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
