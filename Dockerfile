FROM gradle:8.12-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :backend:server:installDist --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/backend/server/build/install/server/ ./
EXPOSE 8080
ENTRYPOINT ["./bin/server"]
