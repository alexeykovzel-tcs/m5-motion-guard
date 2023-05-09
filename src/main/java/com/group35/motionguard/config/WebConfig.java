package com.group35.motionguard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("devices");
        registry.addViewController("/devices").setViewName("devices");
        registry.addViewController("/camera/**").setViewName("camera");
        registry.addViewController("/error").setViewName("error");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register-pi").setViewName("register-pi");
        registry.addViewController("/profile").setViewName("profile");
        registry.addViewController("/detections").setViewName("detections");
        registry.addViewController("/signup").setViewName("signup");
    }
}
