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

# Start with Java 21 image
FROM openjdk:21-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the built JAR file from your local target folder to container
COPY target/fundly-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Set production environment
ENV SPRING_PROFILES_ACTIVE=prod

# Run the JAR file when container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
