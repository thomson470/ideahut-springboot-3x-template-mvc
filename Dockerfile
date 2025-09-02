FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/ideahut-springboot-3x-template-mvc-1.0.0.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar