FROM openjdk:17.0.1-jdk-slim
EXPOSE 8082
ARG JAR_FILE=./build/libs/every-chat-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]