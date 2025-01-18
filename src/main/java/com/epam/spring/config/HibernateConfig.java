package com.epam.spring.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class HibernateConfig {

    private final Environment ENV;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(ENV.getProperty("db.driver"));
        dataSource.setJdbcUrl(ENV.getProperty("db.url"));
        dataSource.setUsername(ENV.getProperty("db.username"));
        dataSource.setPassword(ENV.getProperty("db.password"));
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.epam.spring.model");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", ENV.getProperty("hibernate.show_sql"));
        properties.setProperty("hibernate.format_sql", ENV.getProperty("hibernate.format_sql"));
        properties.setProperty("hibernate.hbm2ddl.auto", ENV.getProperty("hibernate.hbm2ddl.auto"));
        properties.setProperty("hibernate.hbm2ddl.import_files", ENV.getProperty("hibernate.hbm2ddl.import_files"));
        return properties;
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}
