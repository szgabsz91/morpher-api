FROM openjdk:13-jdk-slim

ARG morpherApiVersion

ADD ./build/libs/morpher-api-${morpherApiVersion}.jar /morpher-api.jar
ADD ./data/morpher-system.pb /data/morpher-system.pb

WORKDIR /

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/morpher-api.jar"]
