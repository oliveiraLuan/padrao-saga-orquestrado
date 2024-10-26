FROM openjdk:17-alpine
COPY builds/libs/*.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]