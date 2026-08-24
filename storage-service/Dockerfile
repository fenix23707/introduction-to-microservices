FROM gradle:9.5.0-jdk25-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties gradlew ./
COPY storage-service/build.gradle storage-service/
RUN ./gradlew :storage-service:dependencies --no-daemon -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

COPY storage-service/src storage-service/src
RUN ./gradlew :storage-service:assemble --no-daemon -x test -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

FROM eclipse-temurin:25-jre-alpine-3.23 as run

RUN apk add --no-cache curl

WORKDIR /app
COPY --from=build  /app/storage-service/build/libs/*.jar app.jar

EXPOSE 8085
CMD ["java", "-jar", "app.jar"]