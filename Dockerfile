FROM maven:3-eclipse-temurin-17-alpine as builder
COPY . /tmp/build
WORKDIR /tmp/build
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=builder /tmp/build/edmm-web/target/edmm-web-*.war edmm-web.war
CMD ["java","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","edmm-web.war"]
