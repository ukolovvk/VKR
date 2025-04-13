FROM maven:3.8.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY ./pom.xml .
COPY ./src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/sd-1.0.0.jar /app/app.jar

EXPOSE 7777

ENTRYPOINT ["java", "-jar", "app.jar"]