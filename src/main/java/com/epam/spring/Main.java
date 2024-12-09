package com.epam.spring;

import com.epam.spring.config.AppConfig;
import com.epam.spring.facade.GymCrmFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymCrmFacade facade = context.getBean(GymCrmFacade.class);

        facade.findAllTrainees().forEach(System.out::println);
        facade.findAllTrainers().forEach(System.out::println);
        facade.findAllTrainings().forEach(System.out::println);
    }
}
