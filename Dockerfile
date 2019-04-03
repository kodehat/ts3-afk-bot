FROM gradle:5.3.0-jdk11-slim as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-alpine-slim

LABEL maintainer="dev@codehat.de"
LABEL version="1.0.0"

RUN mkdir /opt/app
COPY --from=builder /home/gradle/src/build/libs/ts3-afk-bot.jar /opt/app/

CMD ["sh", "-c", "java -jar /opt/app/ts3-afk-bot.jar"]
