# WORKFLOW EXPLANATION:
# You create Dockerfile (instructions)
#          ↓
# You push to GitHub
#          ↓
# GitHub Actions reads Dockerfile
#          ↓
# GitHub Actions BUILDS the Docker image automatically
#          ↓
# ✅ Docker image created (you don't have to do anything!)

# Stage 1: Build JAR file using Maven Wrapper and Java 21
FROM eclipse-temurin:21-jdk as build

WORKDIR /app

# Copy Maven wrapper and configuration files
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

# Copy source code
COPY src/ ./src/

# Build the JAR file (will output to /app/target)
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: Create lightweight runtime Java image for application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR file from build stage
COPY --from=build /app/target/fundly-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
