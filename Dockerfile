FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 9100
ADD ./target/oauth-0.0.1-SNAPSHOT.jar oauth-server.jar

ENTRYPOINT ["java", "-jar", "oauth-server.jar"]