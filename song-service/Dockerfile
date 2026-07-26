FROM gradle:9.5.0-jdk25-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties gradlew ./
COPY song-service/build.gradle song-service/
RUN ./gradlew :song-service:dependencies --no-daemon -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

COPY song-service/src song-service/src
RUN ./gradlew :song-service:assemble --no-daemon -x test -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

FROM eclipse-temurin:25-jre-alpine-3.23 as run

WORKDIR /app
COPY --from=build  /app/song-service/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]