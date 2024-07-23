FROM gradle:jdk17-alpine AS builder
COPY --chown=gradle:gradle . /gradle/
WORKDIR /gradle
RUN gradle build --no-daemon

FROM openjdk:17-alpine
RUN mkdir /app
COPY --from=builder /gradle/build/libs/*.jar /app/app.jar
ARG PROFILE
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
EXPOSE 8080