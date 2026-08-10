FROM gradle:9.5.0-jdk25-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties gradlew ./
COPY api-gateway/build.gradle api-gateway/
RUN ./gradlew :api-gateway:dependencies --no-daemon -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

COPY api-gateway/src api-gateway/src
RUN ./gradlew :api-gateway:assemble --no-daemon -x test -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

FROM eclipse-temurin:25-jre-alpine-3.23 as run

RUN apk add --no-cache curl

WORKDIR /app
COPY --from=build  /app/api-gateway/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]