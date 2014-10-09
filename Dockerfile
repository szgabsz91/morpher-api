FROM szgabsz91/jdk-ocamorph-pyphen:12.0.0

ADD ./build/libs/morpher-api-1.0.0-SNAPSHOT.jar /morpher-api.jar
ADD ./data/morpher-system.pb /data/morpher-system.pb

WORKDIR /

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/morpher-api.jar"]
