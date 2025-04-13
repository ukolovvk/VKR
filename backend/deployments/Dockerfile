FROM maven:3.8.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y netcat-traditional
WORKDIR /app

COPY --from=build /app/target/backend-1.0.0.jar app.jar

EXPOSE 7779

ENTRYPOINT ["java", "-jar", "app.jar"]