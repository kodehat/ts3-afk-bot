FROM gradle:6.7.1-jdk11 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

FROM adoptopenjdk/openjdk11:jdk-11.0.9.1_1-alpine-slim

LABEL maintainer="dev@codehat.de"
LABEL version="1.0.0"

RUN mkdir /opt/app
COPY --from=builder /home/gradle/src/build/libs/ts3-afk-bot.jar /opt/app/
WORKDIR /opt/app/

CMD ["sh", "-c", "java -jar ts3-afk-bot.jar"]
