FROM maven:3-jdk-11 as builder
COPY . /tmp/build
WORKDIR /tmp/build
RUN mvn package -DskipTests

FROM adoptopenjdk/openjdk11-openj9:alpine-jre
VOLUME /tmp
COPY --from=builder /tmp/build/edmm-web/target/edmm-web-*.war edmm-web.war
CMD ["java","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","edmm-web.war"]
