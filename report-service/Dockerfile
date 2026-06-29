FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -pl report-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/report-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]