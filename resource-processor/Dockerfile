FROM gradle:9.5.0-jdk25-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties gradlew ./
COPY resource-processor/build.gradle resource-processor/
RUN ./gradlew :resource-processor:dependencies --no-daemon -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

COPY resource-processor/src resource-processor/src
RUN ./gradlew :resource-processor:assemble --no-daemon -x test -Dorg.gradle.jvmargs="-Djava.net.preferIPv4Stack=true" -Dhttp.socketTimeout=120000 -Dhttp.connectionTimeout=120000

FROM eclipse-temurin:25-jre-alpine-3.23 as run

WORKDIR /app
COPY --from=build  /app/resource-processor/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]