FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/chatting-core.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]
