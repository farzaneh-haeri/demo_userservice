FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app
EXPOSE 8081
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]