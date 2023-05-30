FROM openjdk:19-slim

ARG JAR_FILE=build/libs/*SNAPSHOT.jar

COPY $JAR_FILE /api-gateway.jar

ENTRYPOINT exec java -jar /api-gateway.jar