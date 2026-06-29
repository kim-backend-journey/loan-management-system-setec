FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy entire project
COPY . .

# Give execute permission to mvnw
RUN chmod +x mvnw

# Build only auth-service and its dependencies
RUN ./mvnw clean package -pl auth-service -am -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]