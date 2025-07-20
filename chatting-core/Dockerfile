FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/chatting-core.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

ENTRYPOINT ["sh", "-c", "echo SPRING_REDIS_HOST=$SPRING_REDIS_HOST && java -jar app.jar"]
