# ----------- Build Stage -----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom and source code
COPY pom.xml .
COPY src ./src

# Package the app (skip tests for faster build)
RUN mvn clean package -DskipTests


# ----------- Run Stage -----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8282

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
