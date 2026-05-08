FROM bellsoft/liberica-openjdk-alpine:21 AS builder
WORKDIR /app

ARG JAR_FILE=build/libs/backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} backend.jar

RUN java -Djarmode=layertools -jar backend.jar extract

FROM bellsoft/liberica-openjdk-alpine:21
WORKDIR /app

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

COPY build/libs/.env .env
RUN addgroup -S spring && adduser -S spring -G spring \
    && chown spring:spring /app/.env \
    && chmod 640 /app/.env

USER spring

EXPOSE 4001

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]