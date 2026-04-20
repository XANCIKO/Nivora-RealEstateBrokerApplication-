package com.capstone.realestate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final String uploadLocation;

    public StaticResourceConfig(@Value("${app.upload.base-dir:uploads}") String uploadBaseDir) {
        String resourceLocation = Path.of(uploadBaseDir).toAbsolutePath().normalize().toUri().toString();
        this.uploadLocation = resourceLocation.endsWith("/") ? resourceLocation : resourceLocation + "/";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
    }
}
