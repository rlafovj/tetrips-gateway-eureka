FROM openjdk:17-jdk-slim
VOLUME /tmp
RUN apt-get update && apt-get install -y curl
ADD ./build/libs/*.jar config-server.jar
ENTRYPOINT ["java", "-jar", "/config-server.jar"]