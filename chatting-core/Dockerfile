FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/chatting-core.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
