FROM gradle:8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/ktor-app-fat.jar app.jar
COPY firebase-credentials.json /app/firebase-credentials.json

ENV PORT=8080
ENV HOST=0.0.0.0
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/firebase-credentials.json
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]