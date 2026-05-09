FROM gradle:9.5.0-jdk25-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties gradlew ./
COPY service-registry/build.gradle service-registry/
RUN ./gradlew :service-registry:dependencies --no-daemon

COPY service-registry/src service-registry/src
RUN ./gradlew :service-registry:assemble --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine-3.23 as run

WORKDIR /app
COPY --from=build  /app/service-registry/build/libs/*.jar app.jar

EXPOSE 8761
CMD ["java", "-jar", "app.jar"]