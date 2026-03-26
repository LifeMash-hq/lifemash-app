FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN rm -f gradle/gradle-daemon-jvm.properties && ./gradlew :backend:server:installDist --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/backend/server/build/install/server/ ./
EXPOSE 8080
ENTRYPOINT ["./bin/server"]
