FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN mkdir data
COPY --from=build /app/build/libs/nobel-backend.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
