FROM openjdk:21-jdk-slim-bookworm

LABEL maintainer="Gabor Szabo <szgabsz91@gmail.com>"

ARG morpherApiVersion
ARG morpherSystemPath=./data/morpher-system.pb

ADD ./build/libs/morpher-api-${morpherApiVersion}.jar /morpher-api.jar
ADD ${morpherSystemPath} /data/morpher-system.pb

WORKDIR /

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/morpher-api.jar"]
