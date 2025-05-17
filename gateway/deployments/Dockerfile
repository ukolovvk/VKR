FROM node:18 as frontend-builder

WORKDIR /app

COPY front/package*.json ./
RUN npm install

COPY front/* ./
RUN npx vue-cli-service build

FROM maven:3.8.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY gateway/pom.xml .
COPY gateway/src ./src
COPY --from=frontend-builder /app/dist /app/src/main/resources/static

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/gateway-1.0.0.jar app.jar

EXPOSE 7080

ENTRYPOINT ["java", "-jar", "app.jar"]