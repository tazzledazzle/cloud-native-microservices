FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/order-service.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
