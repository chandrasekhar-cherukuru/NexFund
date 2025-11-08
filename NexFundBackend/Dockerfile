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

# Use Eclipse Temurin's Java 21 JDK image (actively maintained and widely used)
FROM eclipse-temurin:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file from your local target folder to the container
COPY target/fundly-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 for Spring Boot
EXPOSE 8080

# Set the active Spring profile to production (adjust if your app uses a different profile)
ENV SPRING_PROFILES_ACTIVE=prod

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
