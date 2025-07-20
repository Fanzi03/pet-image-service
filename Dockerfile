# Сборка jar
FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /app

COPY build.gradle.kts gradle.properties* ./
COPY gradle/ gradle/
COPY gradlew ./

RUN chmod +x ./gradlew 
RUN ./gradlew dependencies --no-daemon --parallel

COPY src/ src/
RUN ./gradlew build -x test --no-daemon --parallel --build-cache

# Runtime слой
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

