FROM openjdk:17-alpine

COPY target/epam-spring-1.0-SNAPSHOT.jar main.jar

ENTRYPOINT ["java","-jar","/main.jar"]
