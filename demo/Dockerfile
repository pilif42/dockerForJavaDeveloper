FROM openjdk:latest
MAINTAINER Philippe Brossier <brossierp@gmail.com>
ARG JAR_FILE
COPY target/${JAR_FILE} /usr/src/demoService.jar
CMD java -jar /usr/src/demoService.jar
