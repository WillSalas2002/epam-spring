package com.epam.spring.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class WebConfig implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        // Create the root application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        // Register the root context as a servlet context parameter
        container.addListener(new ContextLoaderListener(rootContext));

        // Create the dispatcher servlet's application context
        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(WebMvcConfig.class);

        // Register and map the dispatcher servlet
        ServletRegistration.Dynamic dispatcher = container.addServlet(
                "dispatcher", new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

// Register the filter using DelegatingFilterProxy
        container.addFilter("authFilter", new DelegatingFilterProxy("authFilter"))
                .addMappingForUrlPatterns(null, false, "/*");
    }
}
