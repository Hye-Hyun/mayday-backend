FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
