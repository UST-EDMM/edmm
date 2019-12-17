FROM adoptopenjdk/openjdk8-openj9:alpine-jre
VOLUME /tmp
COPY ./target/edmm-web-*.war edmm-web.war
CMD ["java","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","edmm-web.war"]
