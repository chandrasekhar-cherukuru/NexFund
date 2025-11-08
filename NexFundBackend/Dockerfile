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

# Stage 1: build with Maven Wrapper (runs mvnw clean package)
FROM eclipse-temurin:21-jdk as build

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn/
COPY src ./src

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: run app using the produced JAR
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/fundly-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
