# Stage 1: Build jar
FROM maven:3.9.2-eclipse-temurin-17 AS build
# Hoặc dùng maven:3.9.2-jdk-21 nếu tồn tại

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run jar
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
