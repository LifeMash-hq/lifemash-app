FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew :core:ktor:shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/core/ktor/build/libs/lifemash-server.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
