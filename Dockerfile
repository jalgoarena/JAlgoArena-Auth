FROM openjdk:8-jre-alpine

MAINTAINER Jacek Spolnik <jacek.spolnik@gmail.com>

WORKDIR /app
COPY build/libs/jalgoarena-auth-*.jar /app/

VOLUME /app/UserDetailsStore

EXPOSE 5003

CMD java -XX:+PrintFlagsFinal $JAVA_OPTS -jar /app/jalgoarena-auth-*.jar