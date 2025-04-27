FROM openjdk:17-jdk-alpine

COPY build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]