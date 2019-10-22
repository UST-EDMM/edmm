FROM adoptopenjdk/openjdk8-openj9:alpine-jre
VOLUME /edmm
COPY ./edmm-cli/target/edmm-cli-*.jar /edmm/edmm.jar
WORKDIR /edmm
ENTRYPOINT ["java","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","edmm.jar"]
