package com.epam.spring.config;

import com.epam.spring.model.Trainee;
import com.epam.spring.model.Trainer;
import com.epam.spring.model.Training;
import com.epam.spring.model.TrainingType;
import com.epam.spring.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

@Slf4j
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class HibernateConfig {

    private final Environment ENV;

    @Bean
    public SessionFactory sessionFactory() {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Trainee.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(Training.class);
        configuration.addAnnotatedClass(TrainingType.class);
        configuration.addProperties(loadProperties());

        // We need ServiceRegistry in order to obtain more control over hibernate (custom connection pool, caching etc...)
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        log.info("Configuring session factory");

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        // Hibernate JDBC Properties
        properties.setProperty("connection.driver_class", ENV.getProperty("db.driver"));
        properties.setProperty("hibernate.connection.url", ENV.getProperty("db.url"));
        properties.setProperty("hibernate.connection.username", ENV.getProperty("db.username"));
        properties.setProperty("hibernate.connection.password", ENV.getProperty("db.password"));
        // Hibernate Configuration Properties
        properties.setProperty("hibernate.show_sql", ENV.getProperty("hibernate.show_sql"));
        properties.setProperty("hibernate.format_sql", ENV.getProperty("hibernate.format_sql"));
        properties.setProperty("hibernate.hbm2ddl.auto", ENV.getProperty("hibernate.hbm2ddl.auto"));
        properties.setProperty("hibernate.current_session_context_class", ENV.getProperty("hibernate.current_session_context_class"));

        log.info("Setting properties");
        return properties;
    }
}
