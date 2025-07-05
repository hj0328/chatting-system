FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/chatting-api.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
