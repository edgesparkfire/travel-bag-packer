# ==============================================================================
# Dockerfile for Render Web Service (Context: Repository Root)
# ==============================================================================

# Stage 1: Build & Package
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY backend/bag/pom.xml .
COPY backend/bag/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Xss512k -Djava.security.egd=file:/dev/./urandom -jar app.jar"]