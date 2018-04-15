FROM openjdk:8

WORKDIR /app
ADD build/libs/jalgoarena-auth-*.jar /app/
RUN mkdir /app/UserDetailsStore
VOLUME /app/UserDetailsStore

ENV EUREKA_URL=http://eureka:5000/eureka
EXPOSE 5003

CMD java -Dserver.port=5003 -jar /app/jalgoarena-auth-*.jar