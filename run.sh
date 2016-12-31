#!/bin/bash
EUREKA_URL=http://localhost:5000/eureka/
java -Dserver.port=9999 -jar jalgoarena-auth-*.jar
