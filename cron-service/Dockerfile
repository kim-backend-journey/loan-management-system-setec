FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -pl cron-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/cron-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]