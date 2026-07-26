FROM gradle:9.5.0-jdk25-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties gradlew ./

COPY resource-service/build.gradle resource-service/
RUN ./gradlew :resource-service:dependencies --no-daemon -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

COPY resource-service/src resource-service/src
RUN ./gradlew :resource-service:assemble --no-daemon -x test -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

FROM eclipse-temurin:25-jre-alpine-3.23 as run

WORKDIR /app
COPY --from=build  /app/resource-service/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]