FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/user-service.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
