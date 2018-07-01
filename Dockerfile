FROM openjdk:8-jre-alpine

MAINTAINER Jacek Spolnik <jacek.spolnik@gmail.com>

WORKDIR /app
ADD build/libs/jalgoarena-auth-*.jar /app/
RUN mkdir /app/UserDetailsStore
VOLUME /app/UserDetailsStore

EXPOSE 5003

CMD java -jar /app/jalgoarena-auth-*.jar